package view

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.Text
import model.DirectedGraph

@Composable
fun <V, E> MainScreenView(viewModel: MainScreenViewModel<V, E>) {
	Column {
		if (viewModel.graph is DirectedGraph) {
			Button (onClick = viewModel::calculateSCC){
				Text("Calculate SCC")
			}
		}

	}
}
