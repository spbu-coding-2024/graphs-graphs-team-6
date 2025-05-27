import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.graph.DirectedGraph
import model.graph.UndirectedGraph
import space.kscience.kmath.operations.IntRing

val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
	addVertex("A")
	addVertex("B")
	addVertex("C")
	addVertex("D")
	addVertex("E")

	var index = 0

	addEdge("A", "B", index++,4)
	addEdge("B", "C", index++,4)
	addEdge("C", "D", index++,4)
	addEdge("D", "E", index++,4)
	addEdge("E", "A", index++,4)



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
