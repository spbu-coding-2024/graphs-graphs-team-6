package viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import model.Constants.BRIGHT_RED
import model.Constants.SEMI_BLACK
import model.graph.*
import model.json.JsonManager
import model.algos.SCCCalculator
import model.neo4j.GraphService
import model.sqlite.SQLiteManager
import model.algos.BellmanFordPathCalculator
import model.algos.BridgeFinder
import model.algos.CycleDetection
import model.algos.DijkstraPathCalculator
import model.algos.GraphPath
import model.algos.KamadaKawai
import model.algos.MSFFinder
import model.algos.Louvain
import space.kscience.kmath.nd.RingND
import view.loadSQLiteMenu
import java.awt.FileDialog
import java.awt.Frame

enum class Neo4jAction { NONE, LOAD, SAVE }

/**
 * General viewmodel for a program
 *
 * Includes a single graph and it's viewmodel
 *
 * @param graph A graph to visualize
 */
class MainScreenViewModel<V : Any, K : Any, W : Comparable<W>>(graphParam: Graph<V, K, W>) {
	private var _graph = mutableStateOf(graphParam)
	var graph: Graph<V, K, W>
		get() = _graph.value
		set(value) {
			_graph.value = value
		}

	var actionWindowVisibility = mutableStateOf(false)
	var showDbSelectDialog = mutableStateOf(false)
	var saveDialogState = mutableStateOf(false)
	var showLoadSQLiteMenu = mutableStateOf(false)
	private var _showEdgesWeights = mutableStateOf(false)

	var showEdgesWeights: Boolean
		get() = _showEdgesWeights.value
		set(value) {
			_showEdgesWeights.value = value
		}

	var isIncompatibleAlgorithm by mutableStateOf(false)

	val graphViewModel = GraphViewModel(_graph, _showEdgesWeights)

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
			val (predecessors, _) = BellmanFordPathCalculator.bellmanFordAlgorithm(
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
		if (graph is DirectedGraph) {
			val (predecessors, _) = DijkstraPathCalculator().runOn(
				graph,
				startVertex.model.value
			)
			val path = GraphPath.construct(predecessors, endVertex.model.value)
				.map { graphViewModel.getEdgeViewModel(it) }
			ColorUtils.applyOneColor(path, Color.Red)
		}
		else {
			isIncompatibleAlgorithm = true
		}
	}

	fun findCycles(vertex: VertexViewModel<V>) {
		if (graph is DirectedGraph) {
			val cycleDetection = CycleDetection()
			val list =
				cycleDetection.findCyclesFromGivenVertex(
					graph as DirectedGraph,
					vertex.model as DirectedGraph.DirectedVertex
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

	var exceptionMessage: String? by mutableStateOf(null)
	var aboutDialog = mutableStateOf(false)

	fun assignCommunities() {
		if (graph is UndirectedGraph) {
			try {

				val louvainDetector by derivedStateOf { Louvain(graph) }
				val grouping = louvainDetector.detectCommunities()
				val colorMap = ColorUtils.assignColorsGrouped(grouping)
				ColorUtils.applyColors(colorMap, graphViewModel.vertices)
			} catch (e: IllegalArgumentException) {
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
	var pendingNeo4jAction by mutableStateOf(Neo4jAction.NONE)

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
			isIncompatibleAlgorithm = true
			println(e.message)
		}
	}

	fun runSaveSQLiteMenu() {
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

	fun <V : Any, K : Any, W : Comparable<W>>runLoadSQLiteMenu(viewModel: MainScreenViewModel<V, K, W>) {
		val database = SQLiteManager.createConnection()

		val graphList = remember { mutableStateOf<List<String>>(emptyList()) }

		LaunchedEffect(database) {
			graphList.value = SQLiteManager.getGraphNames(database)
		}
		loadSQLiteMenu(viewModel, graphList)
	}

	fun loadSQLite(name: String){
		val database = SQLiteManager.createConnection()
		graph = SQLiteManager.loadGraphFromDatabase(database, name)
	}
}
