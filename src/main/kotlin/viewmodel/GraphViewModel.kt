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
import model.graph.Edge
import model.graph.Graph
import kotlin.random.Random
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import model.graph.Vertex

/**
 * ViewModel for a graph
 *
 * @param graph a state of a graph
 * @param showEdgesWeights a show state of weight labels
 */

class GraphViewModel<V, K, W : Comparable<W>>(
	private val graphState: State<Graph<V, K, W>>,
	showEdgesWeights: State<Boolean>
) {
	val graph: Graph<V, K, W>
		get() = graphState.value

	private val _verticesState by derivedStateOf {
		graph.vertices.associateWith {
			VertexViewModel<V>(
				Random.nextInt(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE).dp,
				Random.nextInt(MIN_RANDOM_VALUE, MAX_RANDOM_VALUE).dp,
				Color(DEFAULT_VERTEX_COLOR),
				Color(DEFAULT_VERTEX_BORDER_COLOR),
				mutableStateOf(DEFAULT_VERTEX_RADIUS.dp),
				DEFAULT_BORDER_WIDTH.dp,
				it
			)
		}
	}


	private val _edgesState by derivedStateOf {
		graph.edges.associateWith {
			EdgeViewModel<V, K, W>(
				_verticesState[it.startVertex] ?: error("Vertex is missing"),
				_verticesState[it.endVertex] ?: error("Vertex is missing"),
				it,
				Color(DEFAULT_EDGE_COLOR),
				DEFAULT_EDGE_WIDTH.dp,
				showEdgesWeights
			)
		}
	}

	/**
	 * Given [edge], return its corresponding view model
	 * @param edge Edge for which a function will find its viewmodel
	 * @return The corresponding edge viewmodel
	 */
	fun getEdgeViewModel(edge: Edge<V, K, W>): EdgeViewModel<V, K, W> {
		return _edgesState[edge] ?: error("Edge does not have its viewmodel")
	}

	/**
	 * Given [vertex], return its corresponding view model
	 *
	 * @param vertex vertex for which a function will find its viewmodel
	 * @return The corresponding vertex viewmodel
	 */
	fun getVertexViewModel(vertex: Vertex<V>): VertexViewModel<V> {
		return _verticesState[vertex] ?: error("Vertex does not have its viewmodel")
	}

	val vertices: Collection<VertexViewModel<V>>
		get() = _verticesState.values

	val edges: Collection<EdgeViewModel<V, K, W>>
		get() = _edgesState.values
}
