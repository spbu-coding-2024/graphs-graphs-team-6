package view.graph

import androidx.compose.runtime.Composable
import viewmodel.DirectedGraphViewModel
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <V,K> DirectedGraphView(
    graphViewModel: DirectedGraphViewModel<K, V>,
) {
    val density = LocalDensity.current
    var mouseOffset by remember { mutableStateOf(Offset.Zero) }
    var modifier = Modifier
        .pointerInput(Unit) {
            detectDragGestures { change, delta ->
                change.consume()
                graphViewModel.vertices.forEach { vertex ->
                    vertex.x += delta.x.dp
                    vertex.y += delta.y.dp
                }
            }
        }
        .onPointerEvent(PointerEventType.Scroll) { it ->
            mouseOffset =  it.changes[0].position
        }
        .scrollable(
            orientation = Orientation.Vertical,
            state = rememberScrollableState { delta ->
                val scale = 1f + delta * .001f
                println(scale)
                graphViewModel.vertices.forEach { vertex ->
                    val x = with(density) { vertex.x.toPx() }
                    val y = with(density) { vertex.y.toPx() }
                    vertex.x = mouseOffset.x.dp * (1 - scale) + (scale * x).dp
                    vertex.y = mouseOffset.y.dp * (1 - scale) + (scale * y).dp
                }
                delta
            }
        )

    graphViewModel.edges.forEach { DirectedEdgeView(it, modifier = modifier) }
    graphViewModel.vertices.forEach { VertexView( it, modifier = modifier) }
}
