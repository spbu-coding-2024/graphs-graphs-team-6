import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import view.graph.DirectedGraphView
import viewmodel.DirectedGraphViewModel
import view.MainScreenView
import viewmodel.MainScreenViewModel


val graph = DirectedGraph<String, Int>().apply {
	addVertex("A")
	addVertex("B")
	addVertex("C")
	addVertex("D")
	addVertex("E")
	addVertex("F")
	addVertex("G")
	addVertex("H")

	addEdge("A", "B", 1)
	addEdge("B", "C", 2)
	addEdge("C", "A", 3)
	addEdge("C", "C", 4)

	addEdge("D", "E", 5)
	addEdge("E", "F", 6)
	addEdge("F", "D", 7)

	// SCC #3: G ↔ H
	addEdge("G", "H", 8)
	addEdge("H", "G", 9)
	}

@Composable
@Preview
fun App() {
	MaterialTheme {
		DirectedGraphView(graphViewModel = DirectedGraphViewModel(graph))
		MainScreenView(MainScreenViewModel(graph))
	}
}

fun main() = application {

		Window(onCloseRequest = ::exitApplication) {
		App()
	}
}
