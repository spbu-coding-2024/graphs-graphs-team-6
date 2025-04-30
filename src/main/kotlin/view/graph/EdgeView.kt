package view.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import model.DirectedGraph
import viewmodel.EdgeViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.PI
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.random.Random

private const val DEFAULT_ARROW_TRIANGLE_HEIGHT = 30
private const val DEFAULT_ARROW_TRIANGLE_WIDTH = 10
private const val DEFAULT_LOOP_RADIUS_COEFF = 0.75f
private const val DEFAULT_LOOP_MULTIPLIER = 3

@Composable
fun <V, K, W: Comparable<W>> showLabel(edgeViewModel: EdgeViewModel<V, K, W>) {
	if (edgeViewModel.weightLabelVisible) {
		Text(
			text = edgeViewModel.weightLabel,
			color = edgeViewModel.color,
			modifier = Modifier.offset(
				x = edgeViewModel.firstVertexViewModel.x +
					(edgeViewModel.secondVertexViewModel.x -
						edgeViewModel.firstVertexViewModel.x)  / 2,
				y = edgeViewModel.firstVertexViewModel.y +
					(edgeViewModel.secondVertexViewModel.y -
						edgeViewModel.firstVertexViewModel.y) / 2 - ((edgeViewModel.hashCode().rem(7) + 1) * 4.5).dp
			)
		)
	}
}

/**
 * Draws an edge (straight line or self-loop) over the full viewport.
 * The passed modifier should handle viewport transformations (drag/zoom).
 */
@Composable
fun <V, K, W : Comparable<W>> EdgeView(
	edgeViewModel: EdgeViewModel<V, K, W>,
	modifier: Modifier = Modifier
) {
	Canvas(modifier = modifier.fillMaxSize()) {
		val first = edgeViewModel.firstVertexViewModel
		val second = edgeViewModel.secondVertexViewModel
		val r = first.radius.toPx()

		if (first != second) {
			val start = Offset(first.x.toPx() + r, first.y.toPx() + r)
			val end = Offset(second.x.toPx() + r, second.y.toPx() + r)
			drawLine(
				start = start,
				end = end,
				color = edgeViewModel.color,
				strokeWidth = edgeViewModel.width.toPx()
			)
			// Draw arrowhead for directed edges
			if (edgeViewModel.model is DirectedGraph.DirectedEdge) {
				val dx = end.x - start.x
				val dy = end.y - start.y
				val angle = atan2(dx, -dy) - PI.toFloat() / 2f
				val sinA = sin(angle)
				val cosA = cos(angle)
				val baseX = end.x - r * cosA
				val baseY = end.y - r * sinA
				val path = androidx.compose.ui.graphics.Path().apply {
					moveTo(baseX, baseY)
					lineTo(
						baseX + -DEFAULT_ARROW_TRIANGLE_HEIGHT * cosA - DEFAULT_ARROW_TRIANGLE_WIDTH * sinA,
						baseY + -DEFAULT_ARROW_TRIANGLE_HEIGHT * sinA + DEFAULT_ARROW_TRIANGLE_WIDTH * cosA
					)
					lineTo(
						baseX + -DEFAULT_ARROW_TRIANGLE_HEIGHT * cosA + DEFAULT_ARROW_TRIANGLE_WIDTH * sinA,
						baseY + -DEFAULT_ARROW_TRIANGLE_HEIGHT * sinA - DEFAULT_ARROW_TRIANGLE_WIDTH * cosA
					)
					close()
				}
				val norm = sqrt(dx * dx + dy * dy)
				if (norm >= 2 * r) drawPath(path, edgeViewModel.color)
			}
		} else {
			val centerX = first.x.toPx() + DEFAULT_LOOP_MULTIPLIER * DEFAULT_LOOP_RADIUS_COEFF * r
			val centerY = first.y.toPx() + DEFAULT_LOOP_MULTIPLIER * DEFAULT_LOOP_RADIUS_COEFF * r
			val loopR = 2 * DEFAULT_LOOP_RADIUS_COEFF * r
			drawArc(
				color = edgeViewModel.color,
				startAngle = 0f,
				sweepAngle = 360f,
				useCenter = false,
				topLeft = Offset(centerX - loopR, centerY - loopR),
				size = androidx.compose.ui.geometry.Size(loopR * 2, loopR * 2),
				style = androidx.compose.ui.graphics.drawscope.Stroke(width = edgeViewModel.width.toPx())
			)
		}
	}
	showLabel(edgeViewModel)
}
