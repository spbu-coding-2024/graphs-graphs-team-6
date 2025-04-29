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
import model.utils.ColorUtils
import viewmodel.GraphViewModel

const val WEIGHT = 9 * 32

val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
}

@Composable
@Preview
fun app() {
	MaterialTheme {
		val graphViewModel = GraphViewModel(graph)
		val colorMap = ColorUtils.assignColorsAll(graph.edges)
		ColorUtils.applyColors(colorMap, graphViewModel.edges)
		MainScreenView<String, Int, Int>(MainScreenViewModel(graph, graphViewModel))
	}
}

fun main() = application {
		Window(onCloseRequest = ::exitApplication) {
		app()
	}
}
