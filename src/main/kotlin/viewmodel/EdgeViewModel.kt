package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import model.DirectedGraph
import model.Edge
import model.UndirectedGraph.UndirectedEdge

class EdgeViewModel<V, K, W: Comparable<W>>(
	val firstVertexViewModel: VertexViewModel<V>,
	val secondVertexViewModel: VertexViewModel<V>,
	model: Edge<V, K, W>,
	color: Color,
	var width: Dp,
	private val weightLabelVisieState: State<Boolean>
): Colorable<Edge<V, K, W>> {

	private val _model = mutableStateOf(model)
	override var model: Edge<V, K, W>
		get() = _model.value
		set(value) { _model.value = value}

	private val _color = mutableStateOf(color)
	override var color: Color
		get() = _color.value
		set(value) {
			_color.value = value
		}

	val weightLabel
		get() = model.weight.toString()

	val weightLabelVisible
		get() = weightLabelVisieState.value
}
