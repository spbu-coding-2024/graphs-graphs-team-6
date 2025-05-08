package view.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.Constants.DEFAULT_ARROW_TRIANGLE_HEIGHT
import model.Constants.DEFAULT_ARROW_TRIANGLE_WIDTH
import model.Constants.DEFAULT_LOOP_MULTIPLIER
import model.Constants.DEFAULT_LOOP_RADIUS_COEFF
import model.Constants.SHOW_LABEL_MOD
import model.DirectedGraph
import viewmodel.EdgeViewModel
import kotlin.math.absoluteValue
import kotlin.math.sqrt
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

@Composable
fun <V, K, W : Comparable<W>> showLabel(edgeViewModel: EdgeViewModel<V, K, W>) {

	if (edgeViewModel.weightLabelVisible) {
		val x1 = edgeViewModel.firstVertexViewModel.x
		val y1 = edgeViewModel.firstVertexViewModel.y
		val x2 = edgeViewModel.secondVertexViewModel.x
		val y2 = edgeViewModel.secondVertexViewModel.y

		val midX = (x1 + x2) / 2f
		val midY = (y1 + y2) / 2f

		val shift: Dp = 8.dp
		val remainder = edgeViewModel.model.hashCode().absoluteValue % SHOW_LABEL_MOD
		val sign = if (remainder == 0 || remainder == 2) 1f else -1f

		val dx = (x2 - x1).value
		val dy = (y2 - y1).value
		val len = sqrt(dx*dx + dy*dy).coerceAtLeast(1f)
		val ux = -dy/len * sign
		val uy =  dx/len * sign

		Text(
			text = edgeViewModel.weightLabel,
			color = edgeViewModel.color,
			modifier = Modifier
				.testTag("EdgeLabel: ${edgeViewModel.model.key}")
				.offset(
				x = midX + (ux * shift.value).dp,
				y = midY + (uy * shift.value).dp
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
	Canvas(modifier = modifier
		.testTag("Edge: ${edgeViewModel.model.key}")
		.fillMaxSize()
	) {
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
