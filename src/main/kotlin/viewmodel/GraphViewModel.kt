package viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Constants.DEFAULT_BORDER_WIDTH
import model.Constants.DEFAULT_EDGE_COLOR
import model.Constants.DEFAULT_EDGE_WIDTH
import model.Constants.DEFAULT_VERTEX_BORDER_COLOR
import model.Constants.DEFAULT_VERTEX_COLOR
import model.Constants.DEFAULT_VERTEX_RADIUS
import model.Constants.MAX_RANDOM_VALUE
import model.Constants.MIN_RANDOM_VALUE
import model.Edge
import model.Graph
import model.Vertex
import kotlin.random.Random




class GraphViewModel<V, K, W: Comparable<W>>(graph: Graph<V, K, W>) {
	private val _vertices = graph.vertices.associateWith {
		VertexViewModel<V, W>(
			Random.nextInt(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE).dp,
			Random.nextInt(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE).dp,
			Color(DEFAULT_VERTEX_COLOR),
			Color(DEFAULT_VERTEX_BORDER_COLOR),
			DEFAULT_VERTEX_RADIUS.dp,
			DEFAULT_BORDER_WIDTH.dp,
			it
		)
	}

	private val _edges = graph.edges.associateWith {
		EdgeViewModel<V, K, W>(
			_vertices[it.pair.toList()[0]] ?: throw error("Vertex is missing"),
			_vertices[it.pair.toList()[if (it.pair.size == 2) 1 else 0]]
				?: throw error("Vertex is missing"),
			it,
			Color(DEFAULT_EDGE_COLOR),
			DEFAULT_EDGE_WIDTH.dp
		)
	}

	/**
	 * Given [edge], return its corresponding view model
	 */
	fun getEdgeViewModel(edge: Edge<V, K, W>): EdgeViewModel<V, K, W> {
		return _edges[edge] ?: error("Edge does not have its viewmodel")
	}

	/**
	 * Given [vertex], return its corresponding view model
	 */
	fun getVertexViewModel(vertex: Vertex<V>): VertexViewModel<V, W> {
		return _vertices[vertex] ?: error("Vertex does not have its viewmodel")
	}

	val vertices: Collection<VertexViewModel<V, W>>
		get() = _vertices.values

	val edges: Collection<EdgeViewModel<V, K, W>>
		get() = _edges.values
}
