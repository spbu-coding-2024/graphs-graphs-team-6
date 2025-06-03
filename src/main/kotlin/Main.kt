import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.GraphGenerator
import view.MainScreenView
import viewmodel.MainScreenViewModel
import model.graph.DirectedGraph
import space.kscience.kmath.operations.IntRing
import view.resetGraphViewModel

val graph = GraphGenerator.generateDirectedGraph()

@Composable
@Preview
fun <V: Any, K: Any, W: Comparable<W>>app(viewModel: MainScreenViewModel<V, K, W>) {
	MaterialTheme {
		MainScreenView(viewModel)
	}
}

fun main() = application {
	val viewModel = MainScreenViewModel(graph)
	Window(onCloseRequest = ::exitApplication,
		title = "Graphs-Graphs") {
		app(viewModel)
		MenuBar {
			Menu("File", mnemonic = 'F') {
				Item("Open", shortcut = KeyShortcut(Key.O, ctrl = true)) { viewModel.showDbSelectDialog.value = true }
				Item("Save", shortcut = KeyShortcut(Key.S, ctrl = true)) { viewModel.saveDialogState.value = true }
			}
			Menu("Graph", mnemonic = 'G') {
				CheckboxItem(
					"Apply algorithm",
					checked = viewModel.actionWindowVisibility.value,
					shortcut = KeyShortcut(Key.A, ctrl = true)
				)
				{
					if (viewModel.actionWindowVisibility.value == true) resetGraphViewModel(viewModel.graphViewModel)
					viewModel.actionWindowVisibility.value = !viewModel.actionWindowVisibility.value
				}
				CheckboxItem(
					"Show weights",
					checked = viewModel.showEdgesWeights,
					shortcut = KeyShortcut(Key.W, ctrl = true)
				)
				{ viewModel.showEdgesWeights = !viewModel.showEdgesWeights }
			}
			Menu("Help", mnemonic = 'H') {
				Item("About") {
					viewModel.aboutDialog.value = true
				}
			}
		}
	}
}
