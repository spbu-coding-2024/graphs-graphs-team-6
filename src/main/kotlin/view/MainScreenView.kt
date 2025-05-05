package view

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import view.graph.GraphView
import androidx.compose.material.Checkbox
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V, K, W : Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val coroutine = rememberCoroutineScope()

	var actionWindowVisibility by remember { mutableStateOf(false) }

	ModalDrawer(
		drawerContent = {
			Column(
				modifier = Modifier
					.testTag("ModalDrawer")
					.padding(16.dp)
			) {
				Button(
					modifier = Modifier,

					onClick = { coroutine.launch { drawerState.close() } }
				) {
					Icon(Icons.Default.Close, "Close")
				}
			}
			drawerButton("Open", description = "OpenButton") {
				coroutine.launch { drawerState.close() }
			}
			drawerButton("Action", icon = Icons.Default.Star, description = "ActionButton") {
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
				modifier = Modifier
					.testTag("MainButton"),
				onClick = {
					if (actionWindowVisibility == true ){
						actionWindowVisibility = false
						resetGraphViewModel(viewModel.graphViewModel)
					} else {
						coroutine.launch { drawerState.open() }
					}
				}
			) {
				Icon(if (actionWindowVisibility == true) Icons.Default.Close else Icons.Default.Menu, "Main button")
			}
		}
		if (viewModel.graphViewModel.vertices.isNotEmpty()) actionMenuView(actionWindowVisibility, viewModel)
	}
	WeightsCheckBox(viewModel)
}


fun drawerShape() = object : Shape {
	override fun createOutline(
		size: Size,
		layoutDirection: LayoutDirection,
		density: Density
	): Outline {
		return Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
	}

}

@Composable
fun drawerButton(
	textString: String,
	icon: ImageVector = Icons.Default.Add,
	description: String,
	onClickMethod: () -> Unit
) {
	Column {
		Button(
			modifier = Modifier
				.width(400.dp)
				.height(100.dp)
				.padding(16.dp)
				.testTag(description),
			onClick = onClickMethod,
			shape = RectangleShape,
		) {
			Icon(icon, description, modifier = Modifier.padding(5.dp))
			Text(textString, fontSize = 20.sp)
		}
	}
}

@Composable
fun <V, K, W : Comparable<W>> WeightsCheckBox(viewModel: MainScreenViewModel<V, K, W>, modifier: Modifier = Modifier) {
	Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
		Row(
			modifier = modifier
				.align(TopEnd),
			verticalAlignment = Alignment.CenterVertically
		) {
			Checkbox(
				modifier = Modifier
					.testTag("WeightCheckBox"),
				checked = viewModel.showEdgesWeights,
				onCheckedChange = { viewModel.showEdgesWeights = it })
			Text("Show weights")
		}
	}
}
