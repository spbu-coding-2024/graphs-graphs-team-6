package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import model.Colorable
import model.DirectedGraph
import model.Edge
import model.UndirectedGraph.UndirectedEdge

class EdgeViewModel<V, K, W: Comparable<W>>(
	val firstVertexViewModel: VertexViewModel<V, W>,
	val secondVertexViewModel: VertexViewModel<V, W>,
	edge: Edge<V, K, W>,
	color: Color,
	var width: Dp,
): Colorable {
	private val _edge = mutableStateOf(edge)
	val isDirected: Boolean
		get() = edge is DirectedGraph.DirectedEdge
	var edge: Edge<V, K, W>
		get() = _edge.value
		set(value) { _edge.value = value}

	private val _color = mutableStateOf(color)
	override var color: Color
		get() = _color.value
		set(value) {
			_color.value = value
		}
}
