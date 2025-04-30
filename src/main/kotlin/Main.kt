import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.UndirectedGraph
import space.kscience.kmath.operations.IntRing
import view.graph.GraphView
import viewmodel.ColorUtils
import viewmodel.GraphViewModel


val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
	addVertex("A")
	addVertex("B")
	addVertex("C")
	addVertex("D")
	addVertex("E")
	addVertex("F")
	addVertex("G")
	addVertex("H")

	var index = 0

	addEdge("A", "B", index++, 3)
	addEdge("B", "C", index++, 2)
	addEdge("C", "A", index++, 61)
	addEdge("C", "C", index++, 12)

	addEdge("C", "F", index++, 23)

	addEdge("D", "E", index++, 1)
	addEdge("E", "F", index++, -1)
	addEdge("F", "D", index++, 5)

	addEdge("H", "D", index++, 5)

	addEdge("G", "H", index++, 12)
	addEdge("H", "G", index, 0)
}

@Composable
@Preview
fun app() {
	MaterialTheme {
		val graphViewModel = GraphViewModel(graph)
		MainScreenView<String, Int, Int>(MainScreenViewModel(graph, graphViewModel))
	}
}

fun main() = application {
		Window(onCloseRequest = ::exitApplication) {
		app()
	}
}
