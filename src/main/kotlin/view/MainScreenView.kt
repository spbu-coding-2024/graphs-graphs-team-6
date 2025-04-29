package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import model.DirectedGraph
import model.UndirectedGraph
import view.graph.GraphView

@Composable
fun <V, K, W: Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	Row {
		Column {
			if (viewModel.graph is DirectedGraph) {
				Button (onClick = viewModel::calculateSCC){
					Text("Calculate SCC")
				}
			}
			if (viewModel.graph is UndirectedGraph) {
				Button(onClick = viewModel::findMSF) {
					Text("Find MSF")
				}
			}
			Checkbox(checked = viewModel.showEdgesWeights, onCheckedChange = {viewModel.showEdgesWeights = it})
		}
		Surface(
			modifier = Modifier.weight(1f)
		) {
			GraphView(viewModel.graphViewModel)
		}
	}
}
