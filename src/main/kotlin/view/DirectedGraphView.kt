package view

import androidx.compose.runtime.Composable
import viewmodel.DirectedGraphViewModel
import VertexView
import androidx.compose.ui.Modifier

@Composable
fun <V,K> DirectedGraphView(
    graphViewModel: DirectedGraphViewModel<K, V>
) {
    graphViewModel.edges.forEach { DirectedEdgeView(it, modifier = Modifier) }
    graphViewModel.vertices.forEach { VertexView( it, modifier = Modifier) }
}