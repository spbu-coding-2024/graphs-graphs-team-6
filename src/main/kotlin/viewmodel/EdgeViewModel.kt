package viewmodel

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
): Colorable<Edge<V, K, W>> {
	private val _model = mutableStateOf(model)
	val isDirected: Boolean
		get() = model is DirectedGraph.DirectedEdge
	override var model: Edge<V, K, W>
		get() = _model.value
		set(value) { _model.value = value}

	private val _color = mutableStateOf(color)
	override var color: Color
		get() = _color.value
		set(value) {
			_color.value = value
		}
}
