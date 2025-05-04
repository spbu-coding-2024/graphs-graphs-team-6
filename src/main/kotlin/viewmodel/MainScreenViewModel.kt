package viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import model.*
import model.Constants.SEMI_BLACK
import model.utils.SCCCalculator
import model.neo4j.GraphService
import model.utils.MSFFinder
import model.utils.Louvain
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring

class MainScreenViewModel<V : Any, K : Any, W : Comparable<W>>(graph: Graph<V, K, W>) {
	var _graph = mutableStateOf(graph)
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

	fun findMSF() {
		val msfFinder by derivedStateOf { MSFFinder(graph) }
		val msf = msfFinder.findMSF()
		edgeColors = msf

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
}
