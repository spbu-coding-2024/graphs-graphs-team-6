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

const val BIG_NUMBER_1 = 9000
const val BIG_NUMBER_2 = 5555

val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
	listOf("A","B","C","D","E","F","G","H").forEach { addVertex(it) }

	var index = 0

	addEdge("A", "B", index, index); index++
	addEdge("B", "C", index, index); index++
	addEdge("C", "A", index, index); index++
	addEdge("C", "C", index, index); index++

	addEdge("D", "E", index, index); index++
	addEdge("E", "F", index, index); index++
	addEdge("F", "D", index, index); index++

	addEdge("G", "H", index, BIG_NUMBER_1); index++
	addEdge("H", "G", index, BIG_NUMBER_2)
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
