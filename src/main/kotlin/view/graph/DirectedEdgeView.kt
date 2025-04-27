package view.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Path
import viewmodel.DirectedEdgeViewModel
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val DEFAULT_ARROW_TRIANGLE_HEIGHT = 30
private const val DEFAULT_ARROW_TRIANGLE_WIDTH = 10
private const val DEFAULT_LOOP_RADIUS_COEFF = 0.75f

@Composable
fun <V, K> DirectedEdgeView(
    dirEdgeViewModel: DirectedEdgeViewModel<V, K>,
    modifier: Modifier
) {

    val firstViewModel by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel) }
    val secondViewModel by remember { mutableStateOf(dirEdgeViewModel.secondVertexViewModel) }

        if (firstViewModel != secondViewModel) {
            drawStraightEdge(dirEdgeViewModel, modifier)
        } else {
            drawDirectedSelfLoop(dirEdgeViewModel, modifier)
        }
}

@Composable
fun <V, K> drawDirectedSelfLoop(
    dirEdgeViewModel: DirectedEdgeViewModel<V, K>,
    modifier: Modifier
) {

    val firstViewModel by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel) }

    val radius by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel.radius) }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = firstViewModel.x.toPx() + 3 * DEFAULT_LOOP_RADIUS_COEFF * radius.toPx()
        val centerY = firstViewModel.y.toPx() + 3 * DEFAULT_LOOP_RADIUS_COEFF * radius.toPx()

        val loopRadius = 2 * DEFAULT_LOOP_RADIUS_COEFF * radius.toPx()

        drawArc(
            color = dirEdgeViewModel.color,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(centerX - loopRadius, centerY - loopRadius - 30f),
            size = androidx.compose.ui.geometry.Size(loopRadius * 2, loopRadius * 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = dirEdgeViewModel.width.toPx())
        )
    }
}

@Composable
fun <V, K> drawStraightEdge(
    dirEdgeViewModel: DirectedEdgeViewModel<V, K>,
    modifier: Modifier
) {

    val firstViewModel by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel) }
    val secondViewModel by remember { mutableStateOf(dirEdgeViewModel.secondVertexViewModel) }

    val radius by remember { mutableStateOf(dirEdgeViewModel.firstVertexViewModel.radius) }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawLine(
            start = Offset(
                firstViewModel.x.toPx() + radius.toPx(),
                firstViewModel.y.toPx() + radius.toPx()
            ),
            end = Offset(
                secondViewModel.x.toPx() + radius.toPx(),
                secondViewModel.y.toPx() + radius.toPx()
            ),
            color = dirEdgeViewModel.color,
            strokeWidth = dirEdgeViewModel.width.toPx()
        )
        val path = Path().apply {
            val angle = (atan2(
                secondViewModel.x.toPx() - firstViewModel.x.toPx(),
                -secondViewModel.y.toPx() + firstViewModel.y.toPx()
            ) - PI / 2).toFloat()
            val sin = sin(angle)
            val cos = cos(angle)
            moveTo(
                secondViewModel.x.toPx() + radius.toPx() - radius.toPx() * cos,
                secondViewModel.y.toPx() + radius.toPx() - radius.toPx() * sin
            )
            val firstCornerX = -DEFAULT_ARROW_TRIANGLE_HEIGHT * cos -
                    DEFAULT_ARROW_TRIANGLE_WIDTH * sin - radius.toPx() * cos
            val firstCornerY = -DEFAULT_ARROW_TRIANGLE_HEIGHT * sin +
                    DEFAULT_ARROW_TRIANGLE_WIDTH * cos - radius.toPx() * sin
            val secondCornerX = -DEFAULT_ARROW_TRIANGLE_HEIGHT * cos +
                    DEFAULT_ARROW_TRIANGLE_WIDTH * sin - radius.toPx() * cos
            val secondCornerY = -DEFAULT_ARROW_TRIANGLE_HEIGHT * sin -
                    DEFAULT_ARROW_TRIANGLE_WIDTH * cos - radius.toPx() * sin

            lineTo(
                secondViewModel.x.toPx() + radius.toPx() + firstCornerX,
                secondViewModel.y.toPx() + radius.toPx() + firstCornerY
            )
            lineTo(
                secondViewModel.x.toPx() + radius.toPx() + secondCornerX,
                secondViewModel.y.toPx() + radius.toPx() + secondCornerY
            )
            close()
        }
        val norm = sqrt(
            (secondViewModel.x.toPx() - firstViewModel.x.toPx()).pow(2)
                    + (secondViewModel.y.toPx() - firstViewModel.y.toPx()).pow(2)
        )
        if (norm >= 2 * radius.toPx()) {
            drawPath(path, dirEdgeViewModel.color)
        }
    }
}
