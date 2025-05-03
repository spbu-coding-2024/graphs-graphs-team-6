package view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.Constants.DEFAULT_BORDER_WIDTH
import model.Constants.DEFAULT_EDGE_COLOR
import model.Constants.DEFAULT_EDGE_WIDTH
import model.Constants.DEFAULT_VERTEX_BORDER_COLOR
import model.Constants.DEFAULT_VERTEX_COLOR
import model.Constants.DEFAULT_VERTEX_RADIUS
import model.UndirectedGraph
import model.utils.Louvain
import model.utils.SSSPCalculator
import viewmodel.ColorUtils
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import viewmodel.VertexViewModel



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V, K, W : Comparable<W>> actionMenuView(actionWindowVisibility: Boolean, viewModel: MainScreenViewModel<V, K, W>) {
	require(viewModel.graph.vertices.isNotEmpty())
	var currentAlgorithm by remember { mutableStateOf(Algorithm.BellmanFord.ordinal) }
	val algorithms = returnArrayOfAlgorithmLabels()
	val arrayOfVertexNames by remember {
		mutableStateOf(
			viewModel.graphViewModel.vertices.map
			{ it.model.value.toString() }.toTypedArray()
		)
	}
	var startVertex by remember { mutableStateOf(viewModel.graphViewModel.vertices.first()) }
	var endVertex by remember { mutableStateOf(viewModel.graphViewModel.vertices.last()) }

	AnimatedVisibility(actionWindowVisibility, Modifier, EnterTransition.None, ExitTransition.None) {
		Row(
			modifier = Modifier.fillMaxSize()
				.padding(20.dp)
				.width(300.dp)
				.height(300.dp),
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.Bottom,
		) {
			menuBox(algorithms[currentAlgorithm], algorithms, algorithms.toTypedArray(),) { i, _ ->
				currentAlgorithm = i
			}
			Button(
				modifier = Modifier
					.padding(5.dp),
				onClick = {
					applyAlgorithm(currentAlgorithm, viewModel, startVertex, endVertex)
				}
			) {
				Icon(Icons.Default.Check, "Apply algorithm")
			}
			if (currentAlgorithm == Algorithm.BellmanFord.ordinal) {
				menuBox(startVertex.model.value.toString(),
					viewModel.graphViewModel.vertices, arrayOfVertexNames) { _, vertex ->
					startVertex = vertex
				}
				menuBox(endVertex.model.value.toString(),
					viewModel.graphViewModel.vertices, arrayOfVertexNames) { _, vertex ->
					endVertex = vertex
				}
			}
			if (viewModel.exceptionMessage == "Incompatible weight type") {
				LouvainAlertDialog(viewModel)
			}
		}
	}
}

fun returnArrayOfAlgorithmLabels(): List<String> {
	return List<String>(Algorithm.entries.size) {
		when (it) {
			Algorithm.BellmanFord.ordinal -> "Bellman-Ford Shortest Path"
			Algorithm.Tarjan.ordinal -> "Tarjan Strong Connected Component"
			Algorithm.Kruskal.ordinal -> "Kruskal Minimal Spanning Tree"
			Algorithm.Louvain.ordinal -> "Louvain Community Detection"
			else -> error("No string for enum")
		}
	}
}

enum class Algorithm {
	Tarjan,
	BellmanFord,
	Kruskal,
	Louvain
}

fun <V, K, W : Comparable<W>> applyAlgorithm(
	algoNum: Int,
	viewModel: MainScreenViewModel<V, K, W>,
	startVertex: VertexViewModel<V>,
	endVertex: VertexViewModel<V>
) {
	resetGraphViewModel(viewModel.graphViewModel)
	when (algoNum) {
		Algorithm.Tarjan.ordinal -> viewModel.calculateSCC()

		Algorithm.BellmanFord.ordinal -> {
			val (predecessors, _) = SSSPCalculator.bellmanFordAlgorithm(
				viewModel.graph,
				startVertex.model.value
			)

			val path = SSSPCalculator.constructPath(predecessors, endVertex.model.value)
				.map { viewModel.graphViewModel.getEdgeViewModel(it) }
			ColorUtils.applyOneColor(path, Color.Red)
		}

		Algorithm.Kruskal.ordinal -> if (viewModel.graph is UndirectedGraph) viewModel.findMSF()

		Algorithm.Louvain.ordinal -> if (viewModel.graph is UndirectedGraph) viewModel.assignCommunities()
	}
}

@Composable
fun <V, K, W: Comparable<W>> LouvainAlertDialog(viewModel: MainScreenViewModel<V, K, W>){
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> menuBox(
	firstTextField: String, collection: Collection<T>,
	arrayOfNames: Array<String>, onClick: (Int, T) -> Unit
) {
	var isExpanded by remember { mutableStateOf(false) }
	ExposedDropdownMenuBox(
		expanded = isExpanded,
		onExpandedChange = {
			isExpanded = !isExpanded
		},
	) {
		TextField(
			value = firstTextField,
			onValueChange = {},
			readOnly = true,
			trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
		)
		ExposedDropdownMenu(
			expanded = isExpanded,
			onDismissRequest = { isExpanded = false }
		) {
			collection.forEachIndexed { i, item ->
				DropdownMenuItem(
					onClick = {
						isExpanded = false
						onClick(i, item)
					}
				) {
					Text(text = arrayOfNames[i])
				}
			}
		}
	}
}

fun <V, K, W : Comparable<W>> resetGraphViewModel(graphViewModel: GraphViewModel<V, K, W>) {
	graphViewModel.vertices.forEach {
		it.color = Color(DEFAULT_VERTEX_COLOR)
		it.borderColor = Color(DEFAULT_VERTEX_BORDER_COLOR)
		it.radius = DEFAULT_VERTEX_RADIUS.dp
		it.borderWidth = DEFAULT_BORDER_WIDTH.dp
	}
	graphViewModel.edges.forEach {
		it.color = Color(DEFAULT_EDGE_COLOR)
		it.width = DEFAULT_EDGE_WIDTH.dp
	}
}
