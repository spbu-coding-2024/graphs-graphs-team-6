package viewmodel

import androidx.compose.ui.unit.dp
import model.Graph
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Vertices
import model.DirectedGraph

private const val MIN_RANDOM_VALUE = 0
private const val MAX_RANDOM_VALUE = 1000
private const val DEFAULT_VERTEX_COLOR = 0xFF2979FF
private const val DEFAULT_VERTEX_BORDER_COLOR = 0xFF2962FF
private const val DEFAULT_WIDTH = 50
private const val DEFAULT_BORDER_WIDTH = 5

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

    val vertices: Collection<VertexViewModel<V>>
        get() = _vertices.values
}
