package viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import model.utils.KamadaKawai
import model.utils.MSFFinder
import model.utils.Louvain
import model.utils.SSSPCalculator

class MainScreenViewModel<V : Any, K : Any, W : Comparable<W>>(graph: Graph<V, K, W>) {
	private var _graph = mutableStateOf(graph)
	var graph: Graph<V, K, W>
		get() = _graph.value
		set(value) {
			_graph.value = value
		}

	private var _showEdgesWeights = mutableStateOf(false)

	var showEdgesWeights
		get() = _showEdgesWeights.value
		set(value) {
			_showEdgesWeights.value = value
		}

	val graphViewModel = GraphViewModel(_graph, _showEdgesWeights)

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
	fun connectNeo4j(uri: String, user: String, password: String) {
		GraphService.uri = uri
		GraphService.user = user
		GraphService.pass = password
	}

	fun loadNeo4j(isDirected: Boolean) {
		graph = GraphService.loadGraph(isDirected)
	}

	fun saveNeo4j(graph: Graph<V, K, W>) {
		GraphService.saveGraph(graph)
	}

	fun drawGraph() {
		val kamadaKawai = KamadaKawai<V, K, W>(graphViewModel)
		kamadaKawai.compute(graphViewModel)
	}
}
