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

const val WEIGHT = 9 * 32

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

	addEdge("A", "B", index++, WEIGHT)
	addEdge("B", "C", index++)
	addEdge("C", "A", index++)
	addEdge("C", "C", index++)

	addEdge("D", "E", index++)
	addEdge("E", "F", index++)
	addEdge("F", "D", index++)

	addEdge("G", "H", index++)
	addEdge("H", "G", index)
	}

@Composable
@Preview
fun app() {
	MaterialTheme {
		val graphViewModel = GraphViewModel(graph)

		val colormap = ColorUtils.assignColorsAll(graph.edges)
		ColorUtils.applyColors(colormap, graphViewModel.edges)

		GraphView(graphViewModel)
		MainScreenView(MainScreenViewModel(graph, graphViewModel))
	}
}

fun main() = application {
		Window(onCloseRequest = ::exitApplication) {
		app()
	}
}
