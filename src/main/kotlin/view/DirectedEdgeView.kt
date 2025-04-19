package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import viewmodel.DirectedEdgeViewModel
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val DEFAULT_ARROW_TRIANGLE_HEIGHT = 30
private const val DEFAULT_ARROW_TRIANGLE_WIDTH = 10


@Composable
fun <V, K> DirectedEdgeView(
    dirEdgeViewModel: DirectedEdgeViewModel<V, K>,
    modifier: Modifier
) {
    var firstX by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel.x) }
    var firstY by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel.y) }

    var secondX by remember { mutableStateOf(dirEdgeViewModel.secondVertexViewModel.x) }
    var secondY by remember { mutableStateOf(dirEdgeViewModel.secondVertexViewModel.y) }

    var radius by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel.radius) }

    Canvas (modifier = modifier.fillMaxSize()) {
        drawLine(
            start = Offset(
                firstX.toPx() + radius.toPx(),
                firstY.toPx() + radius.toPx()
            ),
            end = Offset(
                secondX.toPx() + radius.toPx(),
                secondY.toPx() + radius.toPx()
            ),
            color = dirEdgeViewModel.color,
            strokeWidth = dirEdgeViewModel.width.toPx()
        )
        val path = Path().apply {
            val angle = (atan2(secondX.toPx() - firstX.toPx(), -secondY.toPx() + firstY.toPx())  - Math.PI / 2).toFloat()
            val sin = sin(angle)
            val cos = cos(angle)
            moveTo(secondX.toPx() + radius.toPx() - radius.toPx() * cos, secondY.toPx() + radius.toPx() -radius.toPx() * sin)
            val firstCornerX = -DEFAULT_ARROW_TRIANGLE_HEIGHT * cos -
                    DEFAULT_ARROW_TRIANGLE_WIDTH * sin - radius.toPx() * cos
            val firstCornerY = -DEFAULT_ARROW_TRIANGLE_HEIGHT * sin +
                    DEFAULT_ARROW_TRIANGLE_WIDTH * cos - radius.toPx() * sin
            val secondCornerX = -DEFAULT_ARROW_TRIANGLE_HEIGHT * cos +
                    DEFAULT_ARROW_TRIANGLE_WIDTH * sin - radius.toPx() * cos
            val secondCornerY = -DEFAULT_ARROW_TRIANGLE_HEIGHT * sin -
                    DEFAULT_ARROW_TRIANGLE_WIDTH * cos - radius.toPx() * sin

            lineTo(secondX.toPx() + radius.toPx() + firstCornerX, secondY.toPx() + radius.toPx() + firstCornerY)
            lineTo(secondX.toPx() + radius.toPx() + secondCornerX, secondY.toPx() + radius.toPx() + secondCornerY)
            close()
        }
        drawPath(path, dirEdgeViewModel.color)
    }
}