package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Button
import androidx.compose.material.AlertDialog
import androidx.compose.material.DrawerState
import androidx.compose.material.TextButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Icon
import viewmodel.MainScreenViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import view.graph.GraphView
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import kotlinx.coroutines.CoroutineScope
import model.neo4j.GraphService

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	var isOpen = remember { mutableStateOf(true) }
	if (isOpen.value) {
		Window(onCloseRequest = { isOpen.value = false }) {
			GraphView(viewModel.graphViewModel)
			if (viewModel.graphViewModel.vertices.isNotEmpty()) actionMenuView(viewModel)
			if (viewModel.aboutDialog.value) aboutDialog(viewModel)
			if (viewModel.saveDialogState.value) {
				viewModel.saveJSON()
				viewModel.saveDialogState.value = false
			}
			dbMenu(viewModel)
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
						checked = viewModel.showEdgesWeights.value,
						shortcut = KeyShortcut(Key.W, ctrl = true)
					)
					{ viewModel.showEdgesWeights.value = !viewModel.showEdgesWeights.value }
				}
				Menu("Help", mnemonic = 'H') {
					Item("About") {
						viewModel.aboutDialog.value = true
					}
				}
			}
		}
	}
}

@Composable
fun <V: Any, K: Any, W: Comparable<W>>aboutDialog(viewModel: MainScreenViewModel<V, K, W>) {
	AlertDialog(
		modifier = Modifier
			.testTag("AboutDialog"),
		onDismissRequest = {
			viewModel.aboutDialog.value = false
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
						"Some algorithms require you to choose specific vertices.\n" +
						"\n" +
						"You can also zoom or drag graph.\n"

			)
		},
		confirmButton = {
			TextButton(
				modifier = Modifier
					.testTag("AboutDialogButton"),
				onClick = { viewModel.aboutDialog.value = false }
			) {
				Text("ОК")
			}
		}
	)
}


@Composable
fun <V : Any, K : Any, W : Comparable<W>> dbMenu(
	viewModel: MainScreenViewModel<V, K, W>
) {
	var showNeo4jDialog = remember { mutableStateOf(false) }
	var showOpsDialog = remember { mutableStateOf(false) }


	if (viewModel.showDbSelectDialog.value) {
		AlertDialog(
			modifier = Modifier.testTag("OpenDialog"),
			onDismissRequest = { viewModel.showDbSelectDialog.value = false },
			title = { Text("Select Database") },
			text = {
				Column {
					Spacer(Modifier.height(8.dp))
					Button(
						modifier = Modifier.testTag("Neo4jOpenDialogButton"),
						onClick = {
						viewModel.showDbSelectDialog.value = false
						if (GraphService.sessionFactory == null)
							showNeo4jDialog.value = true
						else
							showOpsDialog.value = true
						}
					) {
						Text("Neo4j")
					}
					Spacer(Modifier.height(8.dp))
					Button(
						modifier = Modifier.testTag("JsonOpenDialogButton"),
						onClick = {
							viewModel.showDbSelectDialog.value = false
							viewModel.loadJSON()
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

