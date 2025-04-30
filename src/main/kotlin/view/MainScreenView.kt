package view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.DrawerValue
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import model.DirectedGraph
import model.utils.SSSPCalculator
import view.graph.GraphView
import viewmodel.GraphViewModel

enum class Algorithm {
	Tarjan,
	BellmanFord
}

fun <V, K, W: Comparable<W>>applyAlgorithm(algoNum: Int, viewModel: MainScreenViewModel<V, K, W>) {
	when (algoNum) {
		Algorithm.Tarjan.ordinal -> viewModel.calculateSCC()

		Algorithm.BellmanFord.ordinal -> {
			val (_, distance) = SSSPCalculator.bellmanFordAlgorithm(
				viewModel.graph,
				viewModel.graph.vertices.first().element
			)
			viewModel.graphViewModel.vertices.forEach { vertexVM ->
				distance[vertexVM.vertex.element]?.let {
					vertexVM.number = it
				}
			}
		}
	}
}

fun drawerShape() = object : Shape {
	override fun createOutline(
		size: Size,
		layoutDirection: LayoutDirection,
		density: Density
	): Outline {
		return Outline.Rectangle(Rect(0f, 0f, size.width / 2,size.height))
	}

}

@Composable
fun drawerButton(textString: String,
                 icon: ImageVector = Icons.Default.Add,
                 description: String = "",
                 onClickMethod: () -> Unit) {
	Column {
		Button(
			modifier = Modifier
				.width(400.dp)
				.height(100.dp)
				.padding(16.dp),
			onClick = onClickMethod,
			shape = RectangleShape,
		){
			Icon(icon, description, modifier = Modifier.padding(5.dp))
			Text(textString, fontSize = 20.sp)
		}
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V, K, W: Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val coroutine = rememberCoroutineScope()

	var currentAlgorithm: Int = Algorithm.BellmanFord.ordinal

	val algorithms = Array<String>(Algorithm.entries.size) {
		when(it) {
			Algorithm.BellmanFord.ordinal -> "Bellman-Ford"
			Algorithm.Tarjan.ordinal -> "Tarjan Strong Connected Component"
			else -> error("No string for enum")
		}
	}

	var actionWindowVisibility by remember { mutableStateOf(false) }
	var menuIsExpanded by remember { mutableStateOf(false) }

	ModalDrawer(
		drawerContent = {
			Column(
				modifier = Modifier
					.padding(16.dp)
			) {
				Button(
					modifier = Modifier,

					onClick = { coroutine.launch { drawerState.close() } }
				) {
					Icon(Icons.Default.Close, "Close")
				}
			}
			drawerButton("Open") {
				coroutine.launch { drawerState.close() }
			}
			drawerButton("Action", icon = Icons.Default.Star) {
				coroutine.launch { drawerState.close() }
				actionWindowVisibility = true
			}
		},
		drawerState = drawerState,
		drawerShape = drawerShape()
	) {
		GraphView(viewModel.graphViewModel)
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
		) {
			Button(
				onClick = {
					if (actionWindowVisibility == true) {
						actionWindowVisibility = false
					} else {
						coroutine.launch { drawerState.open() }
					}
				}
			) {
				Icon(if (actionWindowVisibility == true) Icons.Default.Close else Icons.Default.Menu, "")
			}
		}
		AnimatedVisibility(
			visible = actionWindowVisibility,
			enter = EnterTransition.None,
			exit = ExitTransition.None
		) {
			Row(
				modifier = Modifier
					.fillMaxSize()
					.padding(20.dp)
					.width(300.dp)
					.height(300.dp),
				horizontalArrangement = Arrangement.Start,
				verticalAlignment = Alignment.Bottom,

				) {
				ExposedDropdownMenuBox(
					expanded = menuIsExpanded,
					onExpandedChange = {
						menuIsExpanded = !menuIsExpanded
					},
				) {
					TextField(
						value = algorithms[currentAlgorithm],
						onValueChange = {},
						readOnly = true,
						trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuIsExpanded) }
					)
					ExposedDropdownMenu(
						expanded = menuIsExpanded,
						onDismissRequest = { menuIsExpanded = false },
					) {
						algorithms.forEachIndexed { ordinal, string ->
							DropdownMenuItem(
								onClick = {
									currentAlgorithm = ordinal
									menuIsExpanded = false
								}
							) {
								Text(text = string)
							}
						}

					}
				}
				Button(
					modifier = Modifier
						.padding(5.dp),
					onClick = {
						applyAlgorithm(currentAlgorithm, viewModel)
					}
				) {
					Icon(Icons.Default.Check, "")
				}
			}
		}
	}
}
