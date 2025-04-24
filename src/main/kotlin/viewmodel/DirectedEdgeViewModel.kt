package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Color
import model.DirectedGraph.DirectedEdge

class DirectedEdgeViewModel<V, K>(
    firstVertexViewModel: VertexViewModel<V>,
    secondVertexViewModel: VertexViewModel<V>,
    edge: DirectedEdge<V, K>,
    color: Color,
    width: Dp
): EdgeViewModel<V, K>(firstVertexViewModel, secondVertexViewModel, edge, color, width) {

    private val _edge = mutableStateOf(edge)
    var edge: DirectedEdge<V, K>
        get() = _edge.value
        set(value) { _edge.value = value}
}
