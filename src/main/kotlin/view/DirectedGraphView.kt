package view

import androidx.compose.runtime.Composable
import viewmodel.DirectedGraphViewModel
import VertexView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun <V,K> DirectedGraphView(
    graphViewModel: DirectedGraphViewModel<K, V>,
) {
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
    graphViewModel.edges.forEach { DirectedEdgeView(it, modifier = modifier) }
    graphViewModel.vertices.forEach { VertexView( it, modifier = modifier) }
}