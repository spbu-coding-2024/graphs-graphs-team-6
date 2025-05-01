package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import model.Graph
import model.SCCCalculator
import model.Vertex
import model.Edge
import model.MSFFinder
import model.Louvain

const val SEMI_BLACK = 0x50_00_00_00

class MainScreenViewModel<V, K, W : Comparable<W>>(
	val graph: Graph<V, K, W>,
) {
	private var _showEdgesWeights = mutableStateOf(false)

	var showEdgesWeights
		get() = _showEdgesWeights.value
		set(value) {
			_showEdgesWeights.value = value
		}

	val graphViewModel = GraphViewModel(graph, _showEdgesWeights)

	// Current vertex colorscheme
	var vertexColors by mutableStateOf(mapOf<Vertex<V>, Color>())
		private set

	var edgeColors by mutableStateOf(mapOf<Edge<V, K, W>, Color>())
		private set

	private val calculator = SCCCalculator<V, K, W>()
	fun calculateSCC() {
		vertexColors = calculator.calculateComponents(graph)
		ColorUtils.applyColors(vertexColors, graphViewModel.vertices)
	}

	private val msfFinder = MSFFinder(graph)
	fun findMSF() {
		val msf = msfFinder.findMSF()
		edgeColors = msf

		ColorUtils.applyColors(edgeColors, graphViewModel.edges.sortedBy { it.model.weight }, Color(SEMI_BLACK))
	}

	private val louvainDetector = Louvain(graph)
	fun assignCommunities() {
		val grouping = louvainDetector.detectCommunities()
		val colorMap = ColorUtils.assignColorsGrouped(grouping)

		ColorUtils.applyColors(colorMap, graphViewModel.vertices)
	}
}
