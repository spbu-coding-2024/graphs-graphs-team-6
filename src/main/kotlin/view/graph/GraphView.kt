package view.graph

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.Composable
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
import model.Constants.DEFAULT_ZOOM_SCALE_COEF
import viewmodel.GraphViewModel
import androidx.compose.runtime.State


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <V, K, W: Comparable<W>> GraphView(
	graphViewModel: GraphViewModel<V, K, W>
) {
	val density = LocalDensity.current
	var mouseOffset by remember { mutableStateOf(Offset.Zero) }

	// Modifier handling pan and zoom for the entire graph
	val viewportModifier = Modifier
		.pointerInput(Unit) {
			detectDragGestures { change, delta ->
				change.consume()
				graphViewModel.vertices.forEach {
					it.x += delta.x.dp
					it.y += delta.y.dp
				}
			}
		}
		.onPointerEvent(PointerEventType.Scroll) {
			mouseOffset = it.changes[0].position
		}
		.scrollable(
			orientation = androidx.compose.foundation.gestures.Orientation.Vertical,
			state = rememberScrollableState { delta ->
				val scale = 1f + delta * DEFAULT_ZOOM_SCALE_COEF
				graphViewModel.vertices.forEach {
					val xPx = with(density) { it.x.toPx() }
					val yPx = with(density) { it.y.toPx() }
					it.x = (mouseOffset.x + (xPx - mouseOffset.x) * scale).dp
					it.y = (mouseOffset.y + (yPx - mouseOffset.y) * scale).dp
				}
				delta
			}
		)

	Box(modifier = viewportModifier.fillMaxSize()) {
		// Draw edges using full-viewport canvas in EdgeView
		graphViewModel.edges.forEach { edge ->
			EdgeView(edge)
		}
		// Draw vertices with local offset and size in VertexView
		graphViewModel.vertices.forEach { vertex ->
			VertexView(vertex)
		}
	}
}
