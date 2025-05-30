import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.GraphGenerator
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.graph.DirectedGraph
import space.kscience.kmath.operations.IntRing

val graph = GraphGenerator.generateDirectedGraph()

@Composable
@Preview
fun app() {
	MaterialTheme {
		MainScreenView<String, Int, Int>(MainScreenViewModel(graph))
	}
}

fun main() = application {
	app()
}
