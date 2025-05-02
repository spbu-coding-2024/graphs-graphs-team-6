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
	val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) {it * 2}

	addEdge("A", "B", index, weight[index]); index++
	addEdge("B", "C", index, weight[index]); index++
	addEdge("C", "A", index, weight[index]); index++
	addEdge("C", "C", index, weight[index]); index++

	addEdge("C", "F", index, weight[index]); index++

	addEdge("D", "E", index, weight[index]); index++
	addEdge("E", "F", index, weight[index]); index++
	addEdge("F", "D", index, weight[index]); index++

	addEdge("H", "D", index, weight[index]); index++

	addEdge("G", "H", index, weight[index]); index++
	addEdge("H", "G", index, weight[index]); index++
}

@Composable
@Preview
fun app() {
	MaterialTheme {
		MainScreenView<String, Int, Int>(MainScreenViewModel(graph))
	}
}

fun main() = application {
		Window(onCloseRequest = ::exitApplication) {
		app()
	}
}
