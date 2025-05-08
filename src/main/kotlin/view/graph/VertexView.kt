package view.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import viewmodel.VertexViewModel

@Composable
fun VertexView(
	vertexViewModel: VertexViewModel<*>, modifier: Modifier = Modifier
) {
	Box(
		modifier = modifier
			.offset(x = vertexViewModel.x, y = vertexViewModel.y)
			.testTag("Vertex: ${vertexViewModel.model.value.toString()}")
			.size(vertexViewModel.radius * 2).pointerInput(vertexViewModel) {
				detectDragGestures { change, dragAmount ->
					change.consume()
					vertexViewModel.move(dragAmount)
				}
			}) {
		Canvas(
			modifier = Modifier.fillMaxSize().border(
					BorderStroke(vertexViewModel.borderWidth, vertexViewModel.borderColor),
					CircleShape
				).clip(CircleShape).background(vertexViewModel.color)
		) { }
		Text(
			text = vertexViewModel.model.value.toString(),
			modifier = Modifier.align(Alignment.Center),
			style = MaterialTheme.typography.body1
		)
	}
}
