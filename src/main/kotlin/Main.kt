import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import view.DirectedGraphView
import viewmodel.DirectedGraphViewModel


@Composable
@Preview
fun <V, K>App(graph: DirectedGraph<V, K>) {
	MaterialTheme {
		DirectedGraphView(DirectedGraphViewModel<V,K>(graph))
	}
}

fun main() = application {

	val graph = DirectedGraph<String, Int>()
	graph.addVertex("A")
	graph.addVertex("B")
	graph.addVertex("C")
	graph.addVertex("D")
	graph.addVertex("E")
	graph.addVertex("F")
	graph.addVertex("G")
	graph.addVertex("H")

	graph.addEdge("A", "B", 1)
	graph.addEdge("B", "C", 2)
	graph.addEdge("C", "D", 3)

	Window(onCloseRequest = ::exitApplication) {
		App(graph)
	}
}
