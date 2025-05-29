package viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import model.Constants.BRIGHT_RED
import model.Constants.SEMI_BLACK
import model.graph.DirectedGraph
import model.graph.DirectedGraph.DirectedVertex
import model.graph.Graph
import model.graph.Vertex
import model.graph.Edge
import model.graph.UndirectedGraph
import model.utils.SCCCalculator
import model.neo4j.GraphService
import model.utils.BridgeFinder
import model.utils.CycleDetection
import model.utils.DijkstraPathCalculator
import model.utils.GraphPath
import model.utils.MSFFinder
import model.utils.Louvain
import org.neo4j.ogm.exception.ConnectionException
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring
import model.utils.SSSPCalculator

class MainScreenViewModel<V : Any, K : Any, W : Comparable<W>>(graphParam: Graph<V, K, W>) {
	var graph by mutableStateOf(graphParam)

	private var _showEdgesWeights = mutableStateOf(false)

	var showEdgesWeights: Boolean
		get() = _showEdgesWeights.value
		set(value) {
			_showEdgesWeights.value = value
		}

	var isIncompatibleAlgorithm by mutableStateOf(false)

	val graphViewModel = GraphViewModel(graph, _showEdgesWeights)

	// Current vertex colorscheme
	var vertexColors by mutableStateOf(mapOf<Vertex<V>, Color>())
		private set

	var edgeColors by mutableStateOf(mapOf<Edge<V, K, W>, Color>())
		private set

	fun calculateSCC() {
		if (graph is DirectedGraph) {
			val calculator by derivedStateOf { SCCCalculator<V, K, W>() }
			vertexColors = calculator.calculateComponents(graph)
			ColorUtils.applyColors(vertexColors, graphViewModel.vertices)
		}
		else {
			isIncompatibleAlgorithm = true
		}

	}

	fun findSSSPBellmanFord(startVertex: VertexViewModel<V>, endVertex: VertexViewModel<V>) {
		if (graph is DirectedGraph) {
			val (predecessors, _) = SSSPCalculator.bellmanFordAlgorithm(
				graph,
				startVertex.model.value
			)
			val path = GraphPath.construct(predecessors, endVertex.model.value)
				.map { graphViewModel.getEdgeViewModel(it) }
			ColorUtils.applyOneColor(path, Color.Red)
		} else {
			isIncompatibleAlgorithm = true
		}
	}

	fun findDijkstraPath(startVertex: VertexViewModel<V>, endVertex: VertexViewModel<V>) {
		val (predecessors, _) = DijkstraPathCalculator().runOn(
			graph,
			startVertex.model.value
		)
		val path = GraphPath.construct(predecessors, endVertex.model.value)
			.map { graphViewModel.getEdgeViewModel(it) }
		ColorUtils.applyOneColor(path, Color.Red)
	}

	fun findCycles(vertex: VertexViewModel<V>) {
		if (graph is DirectedGraph) {
			val cycleDetection = CycleDetection()
			val list =
				cycleDetection.findCyclesFromGivenVertex(
					graph as DirectedGraph,
					vertex.model as DirectedVertex
				)

			list.forEachIndexed { i, cycle ->
				val cycleViewModel = cycle.map { graphViewModel.getEdgeViewModel(it) }
				val color = ColorUtils.generateColor(i)
				ColorUtils.applyOneColor(cycleViewModel, color)
			}
		} else {
			isIncompatibleAlgorithm = true
		}

	}

	fun findMSF() {
		if (graph is UndirectedGraph) {
			val msfFinder by derivedStateOf { MSFFinder(graph) }
			val msf = msfFinder.findMSF()
			edgeColors = msf

			ColorUtils.applyColors(
				edgeColors,
				graphViewModel.edges.sortedBy { it.model.weight },
				Color(SEMI_BLACK)
			)
		} else {
			isIncompatibleAlgorithm = true
		}

	}

	private val bridgeFinder = BridgeFinder()
	private fun convertPairsToColorMap(pairs: Set<Pair<Vertex<V>, Vertex<V>>>): Map<Edge<V, K, W>, Color> {
		return graph.edges
			.filterIsInstance<UndirectedGraph.UndirectedEdge<V, K, W>>()
			.filter { edge -> edge.startVertex to edge.endVertex in pairs }
			.associateWith { Color(BRIGHT_RED) }
	}

	fun findBridges() {
		if (graph is UndirectedGraph) {
			val bridges = bridgeFinder.runOn<V, K, W>(graph as UndirectedGraph<V, K, W>)
			edgeColors = convertPairsToColorMap(bridges)

			ColorUtils.applyColors(
				edgeColors,
				graphViewModel.edges.sortedBy { it.model.weight },
				Color(SEMI_BLACK)
			)
		} else {
			isIncompatibleAlgorithm = true
		}
	}

	var exceptionMessage: String? = null
	var showIncompatibleWeightTypeDialog by mutableStateOf(false)

	fun assignCommunities() {
		if (graph is UndirectedGraph) {
			try {

				val louvainDetector by derivedStateOf { Louvain(graph) }
				val grouping = louvainDetector.detectCommunities()
				val colorMap = ColorUtils.assignColorsGrouped(grouping)
				ColorUtils.applyColors(colorMap, graphViewModel.vertices)
			} catch (e: IllegalArgumentException) {
				showIncompatibleWeightTypeDialog = true
				exceptionMessage = e.message
			}
		} else {
			isIncompatibleAlgorithm = true
		}
	}

	// Neo4j
	var showNeo4jConnectionFailedDialog by mutableStateOf(false)
	var showNeo4jOpsDialog by mutableStateOf(false)
	var showNeo4jDialog by mutableStateOf(false)

	fun connectNeo4j(uri: String, user: String, password: String) {
		try {
			GraphService.uri = uri
			GraphService.user = user
			GraphService.pass = password
		} catch (e: IllegalArgumentException) {
			showNeo4jConnectionFailedDialog = true
			exceptionMessage = e.message
		}
	}

	fun loadNeo4j(isDirected: Boolean) {
		graph = GraphService.loadGraph(isDirected)
	}

	fun saveNeo4j(graph: Graph<V, K, W>) {
		GraphService.saveGraph(graph)
	}
}
