package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Vertex
import androidx.compose.ui.graphics.Color
import model.Graph
import viewmodel.ColorUtils
import model.utils.SCCCalculator

class MainScreenViewModel<V, K, W: Comparable<W>>(
	val graph: Graph<V, K, W>,
	val graphViewModel: GraphViewModel<V, K, W>
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
		ColorUtils.applyColors(colors, graphViewModel.vertices)
	}

}
