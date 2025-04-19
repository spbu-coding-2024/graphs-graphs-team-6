package view

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

import viewmodel.DirectedEdgeViewModel

private const val DEFAULT_EDGE_COLOR = 0x000000FF

@Composable
fun <V, K> DirectedEdgeView(
    dirEdgeViewModel: DirectedEdgeViewModel<V, K>,
    modifier: Modifier
) {
    Canvas (modifier = modifier) {
        drawLine(
            start = Offset(dirEdgeViewModel.firstVertexViewModel.x.toPx(),
                dirEdgeViewModel.secondVertexViewModel.x.toPx()),
            end = Offset(dirEdgeViewModel.firstVertexViewModel.y.toPx(),
                dirEdgeViewModel.secondVertexViewModel.y.toPx()),
            color = Color(DEFAULT_EDGE_COLOR),
            strokeWidth = dirEdgeViewModel.width.toPx()
        )
    }
}