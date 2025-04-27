package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Vertex
import androidx.compose.ui.graphics.Color
import model.DirectedGraph

class MainScreenViewModel<V, K>(
	val graph: DirectedGraph<V, K>,
	val graphViewModel: DirectedGraphViewModel<V, K>
) {

	private val calculator = SCCCalculator<V, K>()

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
