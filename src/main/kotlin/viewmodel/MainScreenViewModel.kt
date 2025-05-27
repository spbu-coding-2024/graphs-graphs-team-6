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
import model.utils.GraphPath
import model.utils.MSFFinder
import model.utils.Louvain
import org.neo4j.ogm.exception.ConnectionException
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring
import model.utils.SSSPCalculator

class MainScreenViewModel<V : Any, K : Any, W : Comparable<W>>(graphParam: Graph<V, K, W>) {
	var graph by mutableStateOf(graphParam)

	var showEdgesWeights = mutableStateOf(false)


	val graphViewModel = GraphViewModel(graph, showEdgesWeights)

	// Current vertex colorscheme
	var vertexColors by mutableStateOf(mapOf<Vertex<V>, Color>())
		private set

	var edgeColors by mutableStateOf(mapOf<Edge<V, K, W>, Color>())
		private set

	fun calculateSCC() {
		val calculator by derivedStateOf { SCCCalculator<V, K, W>() }
		vertexColors = calculator.calculateComponents(graph)
		ColorUtils.applyColors(vertexColors, graphViewModel.vertices)
	}

	fun findSSSPBellmanFord(startVertex: VertexViewModel<V>, endVertex: VertexViewModel<V>) {
		val (predecessors, _) = SSSPCalculator.bellmanFordAlgorithm(
			graph,
			startVertex.model.value
		)
		val path = GraphPath.construct(predecessors, endVertex.model.value)
			.map { graphViewModel.getEdgeViewModel(it) }
		ColorUtils.applyOneColor(path, Color.Red)
	}

	fun findCycles(vertex: VertexViewModel<V>) {
		require(graph is DirectedGraph)
		val cycleDetection = CycleDetection()
		val list = cycleDetection.findCyclesFromGivenVertex(graph as DirectedGraph, vertex.model as DirectedVertex)

		list.forEachIndexed { i, cycle ->
			val cycleViewModel = cycle.map { graphViewModel.getEdgeViewModel(it) }
			val color = ColorUtils.generateColor(i)
			ColorUtils.applyOneColor(cycleViewModel, color)
		}

	}

	fun findMSF() {
		val msfFinder by derivedStateOf { MSFFinder(graph) }
		val msf = msfFinder.findMSF()
		edgeColors = msf

		ColorUtils.applyColors(edgeColors, graphViewModel.edges.sortedBy { it.model.weight }, Color(SEMI_BLACK))
	}

	private val bridgeFinder = BridgeFinder()
	private fun convertPairsToColorMap(pairs: Set<Pair<Vertex<V>, Vertex<V>>>): Map<Edge<V, K, W>, Color> {
		return graph.edges
			.filterIsInstance<UndirectedGraph.UndirectedEdge<V, K, W>>()
			.filter { edge -> edge.startVertex to edge.endVertex in pairs }
			.associateWith { Color(BRIGHT_RED) }
	}
	fun findBridges() {
		require(graph is UndirectedGraph)
		val bridges = bridgeFinder.runOn<V, K, W>(graph as UndirectedGraph<V, K, W>)
		edgeColors = convertPairsToColorMap(bridges)

		ColorUtils.applyColors(edgeColors, graphViewModel.edges.sortedBy { it.model.weight }, Color(SEMI_BLACK))
	}

	var exceptionMessage: String? = null
	var showIncompatibleWeightTypeDialog by mutableStateOf(false)

	fun assignCommunities() {
		try {

			val louvainDetector by derivedStateOf { Louvain(graph) }
			val grouping = louvainDetector.detectCommunities()
			val colorMap = ColorUtils.assignColorsGrouped(grouping)
			ColorUtils.applyColors(colorMap, graphViewModel.vertices)
		} catch (e: IllegalArgumentException) {
			showIncompatibleWeightTypeDialog = true
			exceptionMessage = e.message
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
		}
		catch(e: IllegalArgumentException) {
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
