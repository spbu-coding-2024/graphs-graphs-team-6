package viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Graph
import model.Vertex
import kotlin.random.Random

private const val MIN_RANDOM_VALUE = 0
private const val MAX_RANDOM_VALUE = 500
private const val DEFAULT_VERTEX_COLOR = 0xFF2979FF
private const val DEFAULT_VERTEX_BORDER_COLOR = 0xFF2962FF
private const val DEFAULT_WIDTH = 25
private const val DEFAULT_BORDER_WIDTH = 5

private const val DEFAULT_EDGE_WIDTH = 2


class GraphViewModel<V, K, W: Comparable<W>>(graph: Graph<V, K, W>) {
	private val _vertices = graph.vertices.associateWith {
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

	private val _edges = graph.edges.associateWith {
		EdgeViewModel<V, K, W>(
			_vertices[it.pair.toList()[0]] ?: error("Vertex is missing"),
			_vertices[it.pair.toList()[if (it.pair.size == 2) 1 else 0]]
				?: error("Vertex is missing"),
			it,
			Color.Black,
			DEFAULT_EDGE_WIDTH.dp
		)
	}
	val vertices: Collection<VertexViewModel<V>>
		get() = _vertices.values

	val edges: Collection<EdgeViewModel<V, K, W>>
		get() = _edges.values
}
