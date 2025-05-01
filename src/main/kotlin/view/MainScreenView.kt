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
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton

@Composable
fun <V, K, W : Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	Row {
		Column {
			if (viewModel.graph is DirectedGraph) {
				Button(onClick = viewModel::calculateSCC) {
					Text("Calculate SCC")
				}
			}
			if (viewModel.graph is UndirectedGraph) {
				Button(onClick = viewModel::assignCommunities) {
					Text("Detect Communities")
				}
				Button(onClick = viewModel::findMSF) {
					Text("Find MSF")
				}

				if (viewModel.showIncompatibleWeightTypeDialog) {
					AlertDialog(
						onDismissRequest = {
							viewModel.showIncompatibleWeightTypeDialog = false
						},
						title = { Text("Incompatible Edge Weight Type") },
						text = {
							Text(
								"Your graph uses edge weight weight type that is not supported yet. " +
									"Please try exploring graph with numerical weight" +
									"\n${viewModel.exceptionMessage}"
							)
						},
						confirmButton = {
							TextButton(onClick = {
								viewModel.showIncompatibleWeightTypeDialog = false
							}) {
								Text("ОК")
							}
						}
					)
				}
			}
			Row(verticalAlignment = Alignment.CenterVertically) {
				Checkbox(
					checked = viewModel.showEdgesWeights,
					onCheckedChange = { viewModel.showEdgesWeights = it })
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
