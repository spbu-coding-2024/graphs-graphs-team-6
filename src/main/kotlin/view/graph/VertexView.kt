package view.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import viewmodel.VertexViewModel

/**
 * Renders a single vertex as a draggable circle with optional label.
 */
@Composable
fun <V> VertexView(
	vertexViewModel: VertexViewModel<V>,
	modifier: Modifier = Modifier
) {
	// Always read model state directly, do not cache locally
	val x = vertexViewModel.x
	val y = vertexViewModel.y
	val radius = vertexViewModel.radius

	val textMeasurer = rememberTextMeasurer()
	val vertexvalue =  remember(vertexViewModel.model.value.toString()) {
		textMeasurer.measure(vertexViewModel.model.value.toString())
	}


	// Draw circle background and border at (x, y) with size = 2*radius
	Canvas(
		modifier = modifier
			.offset(x, y)
			.size(radius * 2)
			.border(
				BorderStroke(vertexViewModel.borderWidth, vertexViewModel.borderColor),
				shape = CircleShape
			)
			.clip(CircleShape)
			.background(vertexViewModel.color)
			.pointerHoverIcon(PointerIcon.Hand)
			.pointerInput(Unit) {
				detectDragGestures { change, dragAmount ->
					change.consume()
					vertexViewModel.move(dragAmount)
				}
			}
	) {
	}
	Canvas(modifier) {
		drawText(
			textLayoutResult = vertexvalue,
			topLeft = Offset(
				x = vertexViewModel.x.toPx() + vertexViewModel.radius.toPx() - vertexvalue.size.width / 2,
				y = vertexViewModel.y.toPx() + vertexViewModel.radius.toPx() - vertexvalue.size.height / 2,
			),
		)
	}
}
