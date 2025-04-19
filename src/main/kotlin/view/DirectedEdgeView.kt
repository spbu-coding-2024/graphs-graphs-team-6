package view

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import viewmodel.DirectedEdgeViewModel



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

    Canvas (modifier = modifier) {
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
    }
}