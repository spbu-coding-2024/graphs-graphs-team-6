import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.DirectedGraph
import org.neo4j.ogm.session.SessionFactory
import space.kscience.kmath.operations.IntRing

const val TEMP_WEIGHT_VALUE = 231


val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
	listOf("A","B","C","D","E","F","G","H").forEach { addVertex(it) }

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
	addEdge("H", "G", index, TEMP_WEIGHT_VALUE); index++
//	addVertex("X")
//	addVertex("Y")
//	addVertex("Z")
}

@Composable
@Preview
fun app() {
	MaterialTheme {
		MainScreenView<String, Int, Int>(MainScreenViewModel(graph))
	}
}

fun main() = application {
	Window(onCloseRequest = ::exitApplication,
	title = "Graphs-Graphs") {
		app()
	}
}
