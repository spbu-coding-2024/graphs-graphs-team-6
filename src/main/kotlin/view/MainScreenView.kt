package view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.Text
import model.DirectedGraph

@Composable
fun <V, K, W: Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	Column {
		if (viewModel.graph is DirectedGraph) {
			Button (onClick = viewModel::calculateSCC){
				Text("Calculate SCC")
			}
		}

	}
}
