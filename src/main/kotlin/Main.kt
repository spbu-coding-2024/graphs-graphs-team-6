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

const val LARGE_WEIGHT = 10

val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
	listOf("A","B","C","D","E","F","G","H","I","J","K","L")
		.forEach { addVertex(it) }

	var idx = 0

	addEdge("A", "B", idx++, LARGE_WEIGHT)
	addEdge("A", "C", idx++, LARGE_WEIGHT)
	addEdge("B", "C", idx++, LARGE_WEIGHT)
	addEdge("B", "D", idx++, LARGE_WEIGHT)
	addEdge("C", "D", idx++, LARGE_WEIGHT)

	addEdge("E", "F", idx++, LARGE_WEIGHT)
	addEdge("E", "G", idx++, LARGE_WEIGHT)
	addEdge("F", "G", idx++, LARGE_WEIGHT)
	addEdge("F", "H", idx++, LARGE_WEIGHT)
	addEdge("G", "H", idx++, LARGE_WEIGHT)

	addEdge("I", "J", idx++, LARGE_WEIGHT)
	addEdge("I", "K", idx++, LARGE_WEIGHT)
	addEdge("J", "K", idx++, LARGE_WEIGHT)
	addEdge("J", "L", idx++, LARGE_WEIGHT)
	addEdge("K", "L", idx++, LARGE_WEIGHT)

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
