package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.rememberCoroutineScope
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

@Composable
fun <V, K, W: Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val coroutine = rememberCoroutineScope()
	ModalDrawer(
        drawerContent = {
			Column (
				modifier = Modifier
					.padding(16.dp)
			) {
				Button (
					modifier = Modifier,

					onClick = { coroutine.launch { drawerState.close() }}
				) {
					Icon(Icons.Default.Close, "Close")
				}
			}
			drawerButton("Open"){
				coroutine.launch { drawerState.close() }
			}
			drawerButton("SCCC", icon = Icons.Default.Star) {
				coroutine.launch { drawerState.close() }
				viewModel.calculateSCC()
			}
			drawerButton("SSSP", icon = Icons.Default.Star) {
				coroutine.launch { drawerState.close() }
				if (viewModel.graph is DirectedGraph) {
					val (pred, distance) = SSSPCalculator.bellmanFordAlgorithm(viewModel.graph, viewModel.graph.vertices.first().element)
					println(distance)
					viewModel.graphViewModel.vertices.forEach { vertexVM ->
						distance[vertexVM.vertex.element]?.let {
							vertexVM.number = it
						}
					}
				}
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
			Button (
				onClick = { coroutine.launch { drawerState.open() }}
			) {
				Icon(Icons.Default.Menu, "Menu")
			}
		}

	}
}
