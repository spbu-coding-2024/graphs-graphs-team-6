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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import model.Constants.DEFAULT_BORDER_WIDTH
import model.Constants.DEFAULT_EDGE_COLOR
import model.Constants.DEFAULT_EDGE_WIDTH
import model.Constants.DEFAULT_VERTEX_BORDER_COLOR
import model.Constants.DEFAULT_VERTEX_COLOR
import model.Constants.DEFAULT_VERTEX_RADIUS
import model.graph.UndirectedGraph
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import viewmodel.VertexViewModel
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import model.Constants

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>> actionMenuView(
	actionWindowVisibility: Boolean,
	viewModel: MainScreenViewModel<V, K, W>
) {
	require(viewModel.graph.vertices.isNotEmpty())
	var currentAlgorithm by remember { mutableStateOf(Algorithm.BellmanFord.ordinal) }
	val algorithms = Algorithm.entries.map { it.toString() }
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
			modifier = Modifier.fillMaxSize().padding(20.dp).width(300.dp).height(300.dp),
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.Bottom,
		) {
			menuBox(algorithms[currentAlgorithm], algorithms, algorithms.toTypedArray(),
				"Algorithms") { i, _ ->
				currentAlgorithm = i
			}
			Button(
				modifier = Modifier.testTag("ApplyAlgorithm").padding(5.dp),
				onClick = {
					applyAlgorithm(currentAlgorithm, viewModel, startVertex, endVertex)
				}
			) {
				Icon(Icons.Default.Check, "Apply algorithm")
			}
			if (currentAlgorithm == Algorithm.BellmanFord.ordinal ||
				currentAlgorithm == Algorithm.CycleDetection.ordinal ||
				currentAlgorithm == Algorithm.Dijkstra.ordinal
			) {
				menuBox(startVertex.model.value.toString(),
					viewModel.graphViewModel.vertices, arrayOfVertexNames, "StartVertex"
				) { _, vertex -> startVertex = vertex }
			}
			if (currentAlgorithm == Algorithm.BellmanFord.ordinal ||
				currentAlgorithm == Algorithm.Dijkstra.ordinal
			) {
				menuBox(
					endVertex.model.value.toString(),
					viewModel.graphViewModel.vertices, arrayOfVertexNames, "EndVertex"
				) { _, vertex ->
					endVertex = vertex
				}
			}
		}
	}
	if (viewModel.showIncompatibleWeightTypeDialog)
		LouvainAlertDialog(viewModel)
	if (viewModel.isIncompatibleAlgorithm)
		IncompatibilityBanner(viewModel.isIncompatibleAlgorithm, {viewModel.isIncompatibleAlgorithm = false})

}

enum class Algorithm {
	Tarjan,
	BellmanFord,
	Kruskal,
	Louvain,
	CycleDetection,
	Bridges,
	Dijkstra
}

fun <V : Any, K : Any, W : Comparable<W>> applyAlgorithm(
	algoNum: Int,
	viewModel: MainScreenViewModel<V, K, W>,
	startVertex: VertexViewModel<V>,
	endVertex: VertexViewModel<V>
) {
	resetGraphViewModel(viewModel.graphViewModel)
	when (algoNum) {
		Algorithm.BellmanFord.ordinal -> viewModel.findSSSPBellmanFord(startVertex, endVertex)
		Algorithm.CycleDetection.ordinal -> viewModel.findCycles(startVertex)
		Algorithm.Tarjan.ordinal -> viewModel.calculateSCC()
		Algorithm.Kruskal.ordinal -> viewModel.findMSF()
		Algorithm.Bridges.ordinal -> viewModel.findBridges()
		Algorithm.Louvain.ordinal -> viewModel.assignCommunities()
	}
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> LouvainAlertDialog(viewModel: MainScreenViewModel<V, K, W>) {
	AlertDialog(
		modifier = Modifier
			.testTag("AlertDialog"),
		onDismissRequest = {
			viewModel.showIncompatibleWeightTypeDialog = false
		},
		title = { Text("Incompatible Edge Weight Type") },
		text = {
			Text(
				"Your graph uses edge weight type that is not supported yet. " +
					"Please try exploring graph with numerical weight" +
					"\n${viewModel.exceptionMessage}"
			)
		},
		confirmButton = {
			TextButton(
				modifier = Modifier
					.testTag("AlertDialogButton"),
				onClick = { viewModel.showIncompatibleWeightTypeDialog = false }
			) {
				Text("ОК")
			}
		}
	)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> menuBox(
	firstTextField: String, collection: Collection<T>,
	arrayOfNames: Array<String>, description: String,
	onClick: (Int, T) -> Unit
) {
	var isExpanded by remember { mutableStateOf(false) }
	ExposedDropdownMenuBox(
		modifier = Modifier
			.testTag(description),
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
					modifier = Modifier
						.testTag("${description}: ${arrayOfNames[i]}"),
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

//@Composable
//fun <V : Any, K : Any, W : Comparable<W>> incompatibleAlgorithmMenu(viewModel: MainScreenViewModel<V, K, W>) {
//	AlertDialog(
//		onDismissRequest = {
//			viewModel.isIncompatibleAlgorithm = false
//		},
//		title = { Text("Incorrect type of graph") },
//		text = {
//			Text(
//				"This algorithm cannot be applied to graph of this type. Please, try exploring " +
//					if (viewModel.graph is UndirectedGraph) "directed graph" else "undirected graph"
//			)
//		},
//	confirmButton = {
//			TextButton(onClick = {
//				viewModel.isIncompatibleAlgorithm = false
//			}) {
//				Text("ОК")
//			}
//		}
//	)
//}

@Composable
fun IncompatibilityBanner(
	show: Boolean,
	onDismiss: () -> Unit
) {
	AnimatedVisibility(
		visible = show,
		enter = fadeIn() + expandVertically(),
		exit  = fadeOut() + shrinkVertically()
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(Color(Constants.PALE_YELLOW))
				.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Text(
				text = "This algorithm cannot be applied to this graph type",
				color = Color.Black
			)
			TextButton(onClick = onDismiss) {
				Text("OK")
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
