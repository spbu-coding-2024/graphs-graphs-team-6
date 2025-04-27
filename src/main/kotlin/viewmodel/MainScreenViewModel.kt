package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Vertex
import androidx.compose.ui.graphics.Color
import model.DirectedGraph
import model.Graph

class MainScreenViewModel<V, K, W>(
	val graph: Graph<V, K, W>,
	private val graphViewModel: GraphViewModel<V, K, W>
) {

	private val calculator = SCCCalculator<V, K, W>()

	// Current vertex colorscheme
	var colors by mutableStateOf<Map<Vertex<V>, Color>>(emptyMap())
		private set

	init {
		calculator.onComputeListener = { computedColors ->
			colors = computedColors
		}
	}

	fun calculateSCC() {
		calculator.calculateComponents(graph)
		graphViewModel.updateVertexColors(colors)
	}

}
