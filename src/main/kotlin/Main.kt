import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.graph.UndirectedGraph
import space.kscience.kmath.operations.IntRing

const val MAIN_GRAPH_WEIGHT = 4

val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
	addVertex("A")
	addVertex("B")
	addVertex("C")
	addVertex("D")
	addVertex("E")

	var index = 0

	addEdge("A", "B", index++, MAIN_GRAPH_WEIGHT)
	addEdge("B", "C", index++, MAIN_GRAPH_WEIGHT)
	addEdge("C", "D", index++, MAIN_GRAPH_WEIGHT)
	addEdge("D", "E", index++, MAIN_GRAPH_WEIGHT)
	addEdge("E", "A", index++, MAIN_GRAPH_WEIGHT)
}

@Composable
@Preview
fun app() {
	MaterialTheme {
		MainScreenView<String, Int, Int>(MainScreenViewModel(graph))
	}
}

fun main() = application {
	Window(onCloseRequest = ::exitApplication){
		app()
	}
}
