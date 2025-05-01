package view.graph

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import viewmodel.VertexViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun <V, W: Comparable<W>> VertexView(
    vertexViewModel: VertexViewModel<V, W>,
    modifier: Modifier
) {
    var x by remember { mutableStateOf(vertexViewModel.x) }
    var y by remember { mutableStateOf(vertexViewModel.y) }

    var radius by remember { mutableStateOf(vertexViewModel.radius) }

    val textMeasurer = rememberTextMeasurer()
    val vertexElement =  remember(vertexViewModel.vertex.element.toString()) {
        textMeasurer.measure(vertexViewModel.vertex.element.toString())
    }

    Canvas (
        modifier = modifier
            .offset(vertexViewModel.x, vertexViewModel.y)
            .size(radius * 2)
            .border(border = BorderStroke(vertexViewModel.borderWidth,
                vertexViewModel.borderColor), shape = CircleShape)
            .clip(CircleShape)
            .background(vertexViewModel.color)
            .pointerHoverIcon(icon = PointerIcon.Hand, overrideDescendants = true )
            .pointerInput(Unit) {
                detectDragGestures { change, offset ->
                    change.consume()
                    vertexViewModel.move(offset)
                }
            }
    ) {

    }
    Canvas(modifier) {
        drawText(
            textLayoutResult = vertexElement,
            topLeft = Offset(
                x = vertexViewModel.x.toPx() + vertexViewModel.radius.toPx() - vertexElement.size.width / 2,
                y = vertexViewModel.y.toPx() + vertexViewModel.radius.toPx() - vertexElement.size.height / 2,
            ),
        )
    }

}

