import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.DirectedGraph
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.UndirectedGraph
import org.neo4j.ogm.session.SessionFactory
import space.kscience.kmath.operations.IntRing

private const val TEMP_WEIGHT_VALUE = 231
val graph = UndirectedGraph<Int, Int, Int>(IntRing)

@Composable
@Preview
fun app() {
	MaterialTheme {
		val vm = MainScreenViewModel(graph)
 		MainScreenView(vm)
	}
}

fun main() = application {
		Window(onCloseRequest = ::exitApplication) {
		app()
	}
}
