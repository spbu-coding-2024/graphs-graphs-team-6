package view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import viewmodel.MainScreenViewModel
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import view.graph.DirectedGraphView
import viewmodel.DirectedGraphViewModel

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
fun <V, K> MainScreenView(viewModel: MainScreenViewModel<V, K>) {
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
			Column {
				Button(
					modifier = Modifier
						.width(400.dp)
						.height(100.dp)
						.padding(16.dp),
					onClick = { coroutine.launch { drawerState.close() } },
					shape = RectangleShape,
				){
					Icon(Icons.Default.Add, "Open graph using json file", modifier = Modifier.padding(5.dp))
					Text("Open (JSON)", fontSize = 20.sp)
				}
			}
			Column {
				Button(
					modifier = Modifier
						.width(400.dp)
						.height(100.dp)
						.padding(16.dp),
					onClick = {
						coroutine.launch { drawerState.close() }
						viewModel.calculateSCC()
					},
					shape = RectangleShape,
				){
					Icon(Icons.Default.Star, "", modifier = Modifier.padding(5.dp))
					Text("SCCC", fontSize = 20.sp)
				}
			}
		},
        drawerState = drawerState,
		drawerShape = drawerShape()
    ) {


		DirectedGraphView(viewModel.graphViewModel)

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

//	Column(
//	) {
//		Button (onClick = viewModel::calculateSCC){
//			Text("Calculate SCC")
//		}
//	}
}
