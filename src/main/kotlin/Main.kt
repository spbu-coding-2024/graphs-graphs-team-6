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

val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
	listOf("A","B","C","D","E","F","G","H","I","J","K","L")
		.forEach { addVertex(it) }

	var idx = 0

	addEdge("A", "B", idx++, 10)
	addEdge("A", "C", idx++, 10)
	addEdge("B", "C", idx++, 10)
	addEdge("B", "D", idx++, 10)
	addEdge("C", "D", idx++, 10)

	addEdge("E", "F", idx++, 10)
	addEdge("E", "G", idx++, 10)
	addEdge("F", "G", idx++, 10)
	addEdge("F", "H", idx++, 10)
	addEdge("G", "H", idx++, 10)

	addEdge("I", "J", idx++, 10)
	addEdge("I", "K", idx++, 10)
	addEdge("J", "K", idx++, 10)
	addEdge("J", "L", idx++, 10)
	addEdge("K", "L", idx++, 10)

	addEdge("D", "E", idx++, 1)   // 1 <-> 2
	addEdge("H", "I", idx++, 1)   // 2 <-> 3
	addEdge("L", "A", idx, 1)   // 3 <-> 1
}

@Composable
@Preview
fun app() {
	MaterialTheme {
		MainScreenView(MainScreenViewModel(graph))
	}
}

fun main() = application {
		Window(onCloseRequest = ::exitApplication) {
		app()
	}
}
