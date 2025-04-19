import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import viewmodel.VertexViewModel

@Composable
fun <V> VertexView(
    vertexViewModel: VertexViewModel<V>,
    modifier: Modifier
) {
    Canvas (
        modifier = modifier
            .offset(vertexViewModel.x, vertexViewModel.y)
            .size(vertexViewModel.radius)
            .border(border = BorderStroke(vertexViewModel.borderWidth, vertexViewModel.borderColor), shape = CircleShape)
            .clip(CircleShape)
            .background(vertexViewModel.color)
            .pointerHoverIcon(icon = PointerIcon.Hand, overrideDescendants = true )

    ) {

    }
}

