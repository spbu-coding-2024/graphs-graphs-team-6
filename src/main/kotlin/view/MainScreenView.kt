package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Button
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.DrawerState
import androidx.compose.material.TextButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import viewmodel.MainScreenViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import view.graph.GraphView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import kotlinx.coroutines.CoroutineScope
import model.graph.DirectedGraph
import model.JsonManager
import model.graph.Graph
import model.neo4j.GraphService
import space.kscience.kmath.operations.IntRing
import java.awt.FileDialog
import java.awt.Frame

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val coroutine = rememberCoroutineScope()

	var actionWindowVisibility by remember { mutableStateOf(false) }
	var showDbSelectDialog = remember { mutableStateOf(false) }
	var isOpen by remember { mutableStateOf(true) }
	if (isOpen) {
		Window(onCloseRequest = { isOpen = false }) {
			MenuBar {
				Menu("File", mnemonic = 'F') {
					Item("Open", shortcut = KeyShortcut(Key.O, ctrl = true)) { showDbSelectDialog.value = true }
					Item("Save", shortcut = KeyShortcut(Key.S, ctrl = true)) { drawerSave(viewModel) }
				}
				Menu("Graph", mnemonic = 'G') {
					CheckboxItem(
						"Apply algorithm",
						checked = actionWindowVisibility,
						shortcut = KeyShortcut(Key.A, ctrl = true)
					)
					{
						if (actionWindowVisibility == true) resetGraphViewModel(viewModel.graphViewModel)
						actionWindowVisibility = !actionWindowVisibility
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
						viewModel.aboutDialog = true
					}
				}
			}
			GraphView(viewModel.graphViewModel)
			if (viewModel.graphViewModel.vertices.isNotEmpty()) actionMenuView(actionWindowVisibility, viewModel)
			if (viewModel.aboutDialog) aboutDialog(viewModel)
			dbMenu(viewModel, showDbSelectDialog)
		}
	}
}

@Composable
fun <V: Any, K: Any, W: Comparable<W>>aboutDialog(viewModel: MainScreenViewModel<V, K, W>) {
	AlertDialog(
		modifier = Modifier
			.testTag("AboutDialog"),
		onDismissRequest = {
			viewModel.aboutDialog = false
		},
		title = { Text("About") },
		text = {
			Text(
				"Welcome to a graph visualisation program!\n" +
						"\n" +
						"To save or load graph you need to choose \"File\" menu.\n" +
						"You can use JSON or even databases like Neo4j to load/save graphs\n" +
						"Choose \"Graph\" menu and then \"Apply algorithms\" to try some algorithms on a graph, " +
						"a few menus will appear.\n" +
						"Some algorithms require you to choose specific vertices."
			)
		},
		confirmButton = {
			TextButton(
				modifier = Modifier
					.testTag("AboutDialogButton"),
				onClick = { viewModel.aboutDialog = false }
			) {
				Text("ОК")
			}
		}
	)
}


@Composable
fun <V : Any, K : Any, W : Comparable<W>> dbMenu(
	viewModel: MainScreenViewModel<V, K, W>,
	showDbSelectDialog: MutableState<Boolean>
) {
	var showNeo4jDialog = remember { mutableStateOf(false) }
	var showOpsDialog = remember { mutableStateOf(false) }


	if (showDbSelectDialog.value) {
		AlertDialog(
			onDismissRequest = { showDbSelectDialog.value = false },
			title = { Text("Select Database") },
			text = {
				Column {
					Spacer(Modifier.height(8.dp))
					Button(onClick = {
						showDbSelectDialog.value = false
						if (GraphService.sessionFactory == null)
							showNeo4jDialog.value = true
						else
							showOpsDialog.value = true
					}) {
						Text("Neo4j")
					}
					Spacer(Modifier.height(8.dp))
					Button(onClick = {
						showDbSelectDialog.value = false
						val dialog = FileDialog(null as Frame?, "Select JSON")
						dialog.mode = FileDialog.LOAD
						dialog.isVisible = true
						val file = dialog.file
						viewModel.graph = JsonManager.loadJSON<V, K, W>(file)
					}) {
						Text("JSON")
					}
				}
			},
			buttons = {}
		)
	}
	neo4jMenu(viewModel, showNeo4jDialog, showOpsDialog)
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> neo4jMenu(
	viewModel: MainScreenViewModel<V, K, W>,
	showNeo4jDialog: MutableState<Boolean>,
	showOpsDialog: MutableState<Boolean>
) {
	var neo4jUri by remember { mutableStateOf("") }
	var neo4jUser by remember { mutableStateOf("") }
	var neo4jPassword by remember { mutableStateOf("") }
	var neo4jLoadGraphIsDirected = remember { mutableStateOf(false) }

	if (showNeo4jDialog.value && GraphService.sessionFactory == null) {
		AlertDialog(
			onDismissRequest = { showNeo4jDialog.value = false },
			title = { Text("Connect to Neo4j") },
			text = {
				Column(Modifier.fillMaxWidth()) {
					OutlinedTextField(
						value = neo4jUri,
						onValueChange = { neo4jUri = it },
						label = { Text("Bolt URI") },
						modifier = Modifier.fillMaxWidth()
					)
					Spacer(Modifier.height(8.dp))
					OutlinedTextField(
						value = neo4jUser,
						onValueChange = { neo4jUser = it },
						label = { Text("Username") },
						modifier = Modifier.fillMaxWidth()
					)
					Spacer(Modifier.height(8.dp))
					OutlinedTextField(
						value = neo4jPassword,
						onValueChange = { neo4jPassword = it },
						label = { Text("Password") },
						visualTransformation = PasswordVisualTransformation(),
						modifier = Modifier.fillMaxWidth()
					)
				}
			},
			confirmButton = {
				TextButton(onClick = {
					viewModel.connectNeo4j(neo4jUri, neo4jUser, neo4jPassword)
					showNeo4jDialog.value = false
					showOpsDialog.value = true
				}) {
					Text("Connect")
				}
			},
			dismissButton = {
				TextButton(onClick = { showNeo4jDialog.value = false }) {
					Text("Cancel")
				}
			}
		)
	}
	opsDialog(viewModel, showOpsDialog, neo4jLoadGraphIsDirected)

}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> opsDialog(
	viewModel: MainScreenViewModel<V, K, W>,
	showOpsDialog: MutableState<Boolean>,
	neo4jLoadGraphIsDirected: MutableState<Boolean>
) {
	if (showOpsDialog.value) {
		AlertDialog(
			onDismissRequest = { showOpsDialog.value = false },
			title = { Text("Neo4j Operations") },
			text = {
				Column(Modifier.fillMaxWidth().padding(16.dp)) {
					Text("Load graph as...", fontSize = 18.sp)
					Spacer(Modifier.height(12.dp))
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(
							selected = !neo4jLoadGraphIsDirected.value,
							onClick = { neo4jLoadGraphIsDirected.value = false }
						)
						Text("Undirected Graph", Modifier.padding(start = 8.dp))
					}
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(
							selected = neo4jLoadGraphIsDirected.value,
							onClick = { neo4jLoadGraphIsDirected.value = true }
						)
						Text("Directed Graph", Modifier.padding(start = 8.dp))
					}
				}
			},
			confirmButton = {
				TextButton(onClick = {
					viewModel.loadNeo4j(neo4jLoadGraphIsDirected.value)
					showOpsDialog.value = false
				}) {
					Text("Load Graph")
				}
			},
			dismissButton = {
				TextButton(onClick = {
					viewModel.saveNeo4j(viewModel.graph)
					showOpsDialog.value = false
				}) {
					Text("Save Graph")
				}
			}
		)
	}
}

@Composable
fun drawerButton(
	textString: String,
	icon: ImageVector = Icons.Default.Add,
	description: String,
	coroutine: CoroutineScope,
	drawerState: DrawerState,
	onClickMethod: () -> Unit
) {
	Column {
		Button(
			modifier = Modifier
				.width(400.dp)
				.height(100.dp)
				.padding(16.dp)
				.testTag(description),
			onClick = {
				coroutine.launch { drawerState.close() }
				onClickMethod()
			},
			shape = RectangleShape,
		) {
			Icon(icon, description, modifier = Modifier.padding(5.dp))
			Text(textString, fontSize = 20.sp)
		}
	}
}

fun <V: Any, K: Any, W: Comparable<W>> drawerSave(viewModel: MainScreenViewModel<V, K, W>) {
	val extension = ".json"
	val dialog = FileDialog(null as Frame?, "Select JSON")
	dialog.mode = FileDialog.SAVE
	dialog.isVisible = true
	var file = dialog.file
	if (file == null) return
	if (file.length < extension.length || file.substring(file.length - extension.length) != ".json") {
		file += extension
	}
	JsonManager.saveJSON<V,K,W>(file, viewModel.graph)
}
