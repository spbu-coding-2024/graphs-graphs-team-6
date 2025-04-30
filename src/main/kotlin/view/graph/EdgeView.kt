package view.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import viewmodel.EdgeViewModel
import viewmodel.VertexViewModel
import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val DEFAULT_ARROW_TRIANGLE_HEIGHT = 30
private const val DEFAULT_ARROW_TRIANGLE_WIDTH = 10
private const val DEFAULT_LOOP_RADIUS_COEFF = 0.75f
private const val DEFAULT_LOOP_MULTIPLIER = 3

@Composable
fun <V, K, W: Comparable<W>> EdgeView(
	edgeViewModel: EdgeViewModel<V, K, W>,
	modifier: Modifier
) {

	val firstViewModel by remember { mutableStateOf(edgeViewModel.firstVertexViewModel) }
	val secondViewModel by remember { mutableStateOf(edgeViewModel.secondVertexViewModel) }

	if (firstViewModel != secondViewModel) {
		drawStraightEdge(edgeViewModel, modifier)
	} else {
		drawSelfLoop(edgeViewModel, modifier)
	}
}

@Composable
fun <V, K, W: Comparable<W>> drawSelfLoop(
	edgeViewModel: EdgeViewModel<V, K, W>,
	modifier: Modifier
) {

	val firstViewModel by remember { mutableStateOf(edgeViewModel.firstVertexViewModel) }

	val radius by remember { mutableStateOf(edgeViewModel.firstVertexViewModel.radius) }

	Canvas(modifier = modifier.fillMaxSize()) {
		val centerX = firstViewModel.x.toPx() + DEFAULT_LOOP_MULTIPLIER * DEFAULT_LOOP_RADIUS_COEFF * radius.toPx()
		val centerY = firstViewModel.y.toPx() + DEFAULT_LOOP_MULTIPLIER * DEFAULT_LOOP_RADIUS_COEFF * radius.toPx()

		val loopRadius = 2 * DEFAULT_LOOP_RADIUS_COEFF * radius.toPx()

		drawArc(
			color = edgeViewModel.color,
			startAngle = 0f,
			sweepAngle = 360f,
			useCenter = false,
			topLeft = Offset(centerX - loopRadius, centerY - loopRadius),
			size = Size(loopRadius * 2, loopRadius * 2),
			style = Stroke(width = edgeViewModel.width.toPx())
		)
	}
}

fun path(
	firstX: Float,
	firstY: Float,
	secondX: Float,
	secondY: Float,
	radius: Float
): Path {
	val path = Path().apply {
		val angle = (atan2(
			secondX - firstX,
			-secondY + firstY
		) - PI / 2).toFloat()
		val sin = sin(angle)
		val cos = cos(angle)
		moveTo(
			secondX + radius * (1 - cos),
			secondY + radius * (1 - sin)
		)
		val firstCornerX = -DEFAULT_ARROW_TRIANGLE_HEIGHT * cos -
				DEFAULT_ARROW_TRIANGLE_WIDTH * sin - radius * cos
		val firstCornerY = -DEFAULT_ARROW_TRIANGLE_HEIGHT * sin +
				DEFAULT_ARROW_TRIANGLE_WIDTH * cos - radius * sin
		val secondCornerX = -DEFAULT_ARROW_TRIANGLE_HEIGHT * cos +
				DEFAULT_ARROW_TRIANGLE_WIDTH * sin - radius * cos
		val secondCornerY = -DEFAULT_ARROW_TRIANGLE_HEIGHT * sin -
				DEFAULT_ARROW_TRIANGLE_WIDTH * cos - radius * sin

		lineTo(
			secondX + radius + firstCornerX,
			secondY + radius + firstCornerY
		)
		lineTo(
			secondX + radius + secondCornerX,
			secondY + radius + secondCornerY
		)
		close()
	}
	return path
}

@Composable
fun <V, K, W: Comparable<W>> drawStraightEdge(
	edgeViewModel: EdgeViewModel<V, K, W>,
	modifier: Modifier
) {

	val firstViewModel by remember { mutableStateOf(edgeViewModel.firstVertexViewModel) }
	val secondViewModel by remember { mutableStateOf(edgeViewModel.secondVertexViewModel) }
	val radius by remember { mutableStateOf(edgeViewModel.firstVertexViewModel.radius) }
	val textMeasurer = rememberTextMeasurer()
	val textLayout =  remember(edgeViewModel.edge.weight.toString()) {
		textMeasurer.measure(edgeViewModel.edge.weight.toString())
	}

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
			color = edgeViewModel.color,
			strokeWidth = edgeViewModel.width.toPx()
		)

		if (edgeViewModel.isDirected) {
			val path = path(
				firstViewModel.x.toPx(),
				firstViewModel.y.toPx(),
				secondViewModel.x.toPx(),
				secondViewModel.y.toPx(),
				radius.toPx()
			)
			val norm = sqrt(
				(secondViewModel.x.toPx() - firstViewModel.x.toPx()).pow(2)
						+ (secondViewModel.y.toPx() - firstViewModel.y.toPx()).pow(2)
			)
			if (norm >= 2 * radius.toPx()) {
				drawPath(path, edgeViewModel.color)
			}

			drawText(
                textLayoutResult = textLayout,
                topLeft = Offset(
					(firstViewModel.x.toPx() + secondViewModel.x.toPx()) / 2,
					(firstViewModel.y.toPx() + secondViewModel.y.toPx()) / 2,
				),
            )
		}
	}
}
