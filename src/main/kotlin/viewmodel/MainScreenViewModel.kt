package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Vertex
import androidx.compose.ui.graphics.Color
import model.DirectedGraph
import model.Graph
import model.Edge
import viewmodel.ColorUtils

class MainScreenViewModel<V, K, W: Comparable<W>>(
	val graph: Graph<V, K, W>,
	private val graphViewModel: GraphViewModel<V, K, W>
) {
	// Current vertex colorscheme
	var vertexColors by mutableStateOf(mapOf<Vertex<V>, Color>())
		private set

	var edgeColors by mutableStateOf(mapOf<Edge<V, K, W>, Color>())
		private set

	private val calculator = SCCCalculator<V, K, W>()
	fun calculateSCC() {
		vertexColors  = calculator.calculateComponents(graph)
		ColorUtils.applyColors(vertexColors, graphViewModel.vertices)
	}

	private val msfFinder = MSFFinder(graph)
	fun findMSF() {
		edgeColors = msfFinder.findMSF()
		ColorUtils.applyColors(edgeColors, graphViewModel.edges.sortedBy { it.model.weight }, Color(0x50_00_00_00))
	}

}
