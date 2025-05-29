package viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import model.Constants.BRIGHT_RED
import model.Constants.SEMI_BLACK
import model.JsonManager
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
import model.utils.BellmanFordPathCalculator
import java.awt.FileDialog
import java.awt.Frame

/**
 * General viewmodel for a program
 *
 * Includes a single graph and it's viewmodel
 *
 * @param graph A graph to visualize
 */
class MainScreenViewModel<V : Any, K : Any, W : Comparable<W>>(graph: Graph<V, K, W>) {
	private var _graph = mutableStateOf(graph)
	var graph: Graph<V, K, W>
		get() = _graph.value
		set(value) {
			_graph.value = value
		}

	var showEdgesWeights = mutableStateOf(false)
	var actionWindowVisibility = mutableStateOf(false)
	var showDbSelectDialog = mutableStateOf(false)
	var saveDialogState = mutableStateOf(false)
	val graphViewModel = GraphViewModel(_graph, showEdgesWeights)

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
		val (predecessors, _) = BellmanFordPathCalculator.bellmanFordAlgorithm(
			graph,
			startVertex.model.value
		)
		val path = GraphPath.construct(predecessors, endVertex.model.value)
			.map { graphViewModel.getEdgeViewModel(it) }
		ColorUtils.applyOneColor(path, Color.Red)
	}

	fun findCycles(vertex: VertexViewModel<V>) {
		val cycleDetection = CycleDetection()
		val list = cycleDetection.findCyclesFromGivenVertex(graph, vertex.model)

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

	var exceptionMessage: String? by mutableStateOf(null)
	var aboutDialog = mutableStateOf(false)

	fun assignCommunities() {
		try {
			val louvainDetector by derivedStateOf { Louvain(graph) }
			val grouping = louvainDetector.detectCommunities()
			val colorMap = ColorUtils.assignColorsGrouped(grouping)
			ColorUtils.applyColors(colorMap, graphViewModel.vertices)
		} catch (e: IllegalArgumentException) {
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

	/**
	 * Opens a dialog to load graph form a json
	 */
	fun loadJSON() {
		val dialog = FileDialog(null as Frame?, "Select JSON")
		dialog.mode = FileDialog.LOAD
		dialog.isVisible = true
		val file = dialog.file
		graph = JsonManager.loadJSON<V, K, W>(file)
	}

	/**
	 * Opens a dialog to save graph into a json
	 */
	fun saveJSON() {
		val extension = ".json"
		val dialog = FileDialog(null as Frame?, "Save JSON")
		dialog.mode = FileDialog.SAVE
		dialog.isVisible = true
		var file = dialog.file
		if (file == null) return
		if (file.length < extension.length || file.substring(file.length - extension.length) != ".json") {
			file += extension
		}
		JsonManager.saveJSON<V,K,W>(file, graph)
	}

	/**
	 * Applies graph drawing
	 *
	 * On error, sets [exceptionMessage] with exception message
	 */
	fun drawGraph() {
		val kamadaKawai = KamadaKawai<V, K, W>(graphViewModel)
		try {
			kamadaKawai.compute(graphViewModel)
		} catch(e: IllegalArgumentException) {
			exceptionMessage = e.message
		}
	}
}
