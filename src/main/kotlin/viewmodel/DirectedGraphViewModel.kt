package viewmodel

import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import model.DirectedGraph
import model.Vertex

private const val MIN_RANDOM_VALUE = 0
private const val MAX_RANDOM_VALUE = 500
private const val DEFAULT_VERTEX_COLOR = 0xFF2979FF
private const val DEFAULT_VERTEX_BORDER_COLOR = 0xFF2962FF
private const val DEFAULT_WIDTH = 25
private const val DEFAULT_BORDER_WIDTH = 5

private const val DEFAULT_EDGE_WIDTH = 2

class DirectedGraphViewModel<V, K>(
	graph: DirectedGraph<V, K>
) {
	private val _vertices = graph.vertices.associateWith { it ->
		VertexViewModel<V>(
			Random.nextInt(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE).dp,
			Random.nextInt(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE).dp,
			Color(DEFAULT_VERTEX_COLOR),
			Color(DEFAULT_VERTEX_BORDER_COLOR),
			DEFAULT_WIDTH.dp,
			DEFAULT_BORDER_WIDTH.dp,
			it
		)
	}

	private val _edges = graph.edges.associateWith { it ->
		DirectedEdgeViewModel<V, K>(
			_vertices[it.firstVertex] ?: error("Vertex is missing"),
			_vertices[it.secondVertex] ?: error("Vertex is missing"),
			it,
			Color.Black,
			DEFAULT_EDGE_WIDTH.dp
		)
	}
	val vertices: Collection<VertexViewModel<V>>
		get() = _vertices.values

	val edges: Collection<DirectedEdgeViewModel<V, K>>
		get() = _edges.values

	internal fun updateVertexColors(colorMap: Map<Vertex<V>, Color>) {
		_vertices.forEach { (modelVertex, vm) ->
			vm.color = colorMap[modelVertex]
				?: error("Missing color for $modelVertex")
		}
	}

}
