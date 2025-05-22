import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.graph.DirectedGraph
import space.kscience.kmath.operations.IntRing

const val TEMP_WEIGHT_VALUE = 231


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
	val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) {it * 2 + 1}

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
	Window(onCloseRequest = ::exitApplication){
		app()
	}
}
