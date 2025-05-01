package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.DirectedGraph
import model.UndirectedGraph
import view.graph.GraphView

@Composable
fun <V, K, W: Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	Row {
		Column {
			Row(verticalAlignment = Alignment.CenterVertically) {
				if (viewModel.graph is DirectedGraph) {
					Button(onClick = viewModel::calculateSCC) {
						Text("Calculate SCC")
					}
				}
				if (viewModel.graph is UndirectedGraph) {
					Button(onClick = viewModel::findMSF) {
						Text("Find MSF")
					}
				}
			}

			if (viewModel.graph is UndirectedGraph) {
				Row(verticalAlignment = Alignment.CenterVertically) {
					Button(onClick = viewModel::findBridges) {
						Text("Find Bridges")
					}
				}
			}

			Row(verticalAlignment = Alignment.CenterVertically) {
				Checkbox(checked = viewModel.showEdgesWeights, onCheckedChange = {viewModel.showEdgesWeights = it})
				Text(text = "Show weights", modifier = Modifier.padding(2.dp))
			}
		}
		Surface(
			modifier = Modifier.weight(1f)
		) {
			GraphView(viewModel.graphViewModel)
		}
	}
}
