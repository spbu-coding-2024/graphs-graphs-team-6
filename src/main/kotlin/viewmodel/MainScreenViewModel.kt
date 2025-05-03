package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import model.BridgeFinder
import model.Constants.SEMI_BLACK
import model.Constants.BRIGHT_RED
import model.Graph
import model.Vertex
import model.Edge
import model.UndirectedGraph
import model.utils.SCCCalculator
import model.utils.MSFFinder

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

	private val bridgeFinder = BridgeFinder()
	private fun convertPairsToColorMap(pairs: Set<Set<Vertex<V>>>): Map<Edge<V, K, W>, Color> {
		return graph.edges
			.filterIsInstance<UndirectedGraph.UndirectedEdge<V, K, W>>()
			.filter { edge -> edge.pair in pairs }
			.associateWith { Color(BRIGHT_RED) }
	}
	fun findBridges() {
		require(graph is UndirectedGraph)
		val bridges = bridgeFinder.runOn(graph)
		edgeColors = convertPairsToColorMap(bridges)

		ColorUtils.applyColors(edgeColors, graphViewModel.edges.sortedBy { it.model.weight }, Color(SEMI_BLACK))
	}

}
