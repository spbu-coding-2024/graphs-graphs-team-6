package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import model.Edge

abstract class EdgeViewModel<V, K> (
    val firstVertexViewModel: VertexViewModel<V>,
    val secondVertexViewModel: VertexViewModel<V>,
    edge: Edge<V, K>,
    color: Color,
    var width: Dp,
) {
    private val _color = mutableStateOf(color)
    var color: Color
        get() = _color.value
        set(value) { _color.value = value }
}