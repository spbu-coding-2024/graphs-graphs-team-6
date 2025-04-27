package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import model.DirectedGraph
import model.Edge
import model.UndirectedGraph.UndirectedEdge

class EdgeViewModel<V, K>(
	val firstVertexViewModel: VertexViewModel<V>,
	val secondVertexViewModel: VertexViewModel<V>,
	edge: Edge<V, K>,
	color: Color,
	var width: Dp,
) {
	private val _edge = mutableStateOf(edge)
	val isDirected: Boolean
		get() = edge is DirectedGraph.DirectedEdge
	var edge: Edge<V, K>
		get() = _edge.value
		set(value) { _edge.value = value}

	private val _color = mutableStateOf(color)
	var color: Color
		get() = _color.value
		set(value) {
			_color.value = value
		}
}
