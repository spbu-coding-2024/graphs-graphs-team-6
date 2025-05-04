package view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import viewmodel.MainScreenViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
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
import model.utils.SSSPCalculator
import view.graph.GraphView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd
import model.Graph
import model.neo4j.GraphService

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
	val drawerState = rememberDrawerState(DrawerValue.Closed)
	val coroutine = rememberCoroutineScope()

	var actionWindowVisibility by remember { mutableStateOf(false) }
	var showDbSelectDialog by remember { mutableStateOf(false) }
	var showNeo4jDialog by remember { mutableStateOf(false) }
	var showOpsDialog by remember { mutableStateOf(false) }
	var neo4jUri by remember { mutableStateOf("") }
	var neo4jUser by remember { mutableStateOf("") }
	var neo4jPassword by remember { mutableStateOf("") }
	var neo4jLoadGraphIsDirected by remember { mutableStateOf(false) }


	fun drawerShape() = object : Shape {
		override fun createOutline(
			size: Size,
			layoutDirection: LayoutDirection,
			density: Density
		): Outline {
			return Outline.Rectangle(Rect(0f, 0f, size.width / 2, size.height))
		}

	}

	@Composable
	fun drawerButton(
		textString: String,
		icon: ImageVector = Icons.Default.Add,
		description: String,
		onClickMethod: () -> Unit
	) {
		Column {
			Button(
				modifier = Modifier
					.width(400.dp)
					.height(100.dp)
					.padding(16.dp)
					.testTag(description),
				onClick = onClickMethod,
				shape = RectangleShape,
			) {
				Icon(icon, description, modifier = Modifier.padding(5.dp))
				Text(textString, fontSize = 20.sp)
			}
		}
	}

	@Composable
	fun <V : Any, K : Any, W : Comparable<W>> WeightsCheckBox(
		viewModel: MainScreenViewModel<V, K, W>,
		modifier: Modifier = Modifier
	) {
		Box(modifier = modifier.fillMaxSize().padding(16.dp)) {
			Row(modifier = modifier.align(TopEnd), verticalAlignment = Alignment.CenterVertically) {
				Checkbox(
					checked = viewModel.showEdgesWeights,
					onCheckedChange = { viewModel.showEdgesWeights = it })
				Text("Show weights")
			}
		}
	}

	ModalDrawer(
		drawerContent = {
			Column(
				modifier = Modifier
					.testTag("ModalDrawer")
					.padding(16.dp)
			) {
				Button(onClick = { coroutine.launch { drawerState.close() } }) {
					Icon(Icons.Default.Close, "Close")
				}
				Spacer(Modifier.height(8.dp))
				drawerButton("Open", description = "OpenButton") {
					coroutine.launch { drawerState.close() }
					showDbSelectDialog = true
				}
				drawerButton("Action", icon = Icons.Default.Star, description = "ActionButton") {
					coroutine.launch { drawerState.close() }
					actionWindowVisibility = true
				}
			}
		},
		drawerState = drawerState,
		drawerShape = drawerShape()
	) {
		GraphView(viewModel.graphViewModel)
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp)
				.testTag("MainButton")
		) {
			Button(
				onClick = {
					if (actionWindowVisibility && viewModel.graphViewModel.vertices.isNotEmpty()) {
						actionWindowVisibility = false
						resetGraphViewModel(viewModel.graphViewModel)
					} else if (viewModel.graphViewModel.vertices.isNotEmpty()) {
						coroutine.launch { drawerState.open() }
					}
				}
			) {
				Icon(
					if (actionWindowVisibility) Icons.Default.Close else Icons.Default.Menu,
					"Main button"
				)
			}
		}
		if (viewModel.graphViewModel.vertices.isNotEmpty()) {
			actionMenuView(actionWindowVisibility, viewModel)
		}
	}

	WeightsCheckBox(viewModel)

	if (showDbSelectDialog) {
		AlertDialog(
			onDismissRequest = { showDbSelectDialog = false },
			title = { Text("Select Database") },
			text = {
				Column {
					Spacer(Modifier.height(8.dp))
					Button(onClick = {
						showDbSelectDialog = false
						if (GraphService.sessionFactory == null)
							showNeo4jDialog = true
						else
							showOpsDialog = true
					}) {
						Text("Neo4j")
					}
				}
			},
			buttons = {}
		)
	}
	if (showNeo4jDialog && GraphService.sessionFactory == null) {
		AlertDialog(
			onDismissRequest = { showNeo4jDialog = false },
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
					showNeo4jDialog = false
					showOpsDialog = true
				}) {
					Text("Connect")
				}
			},
			dismissButton = {
				TextButton(onClick = { showNeo4jDialog = false }) {
					Text("Cancel")
				}
			}
		)
	}
	if (showOpsDialog) {
		AlertDialog(
			onDismissRequest = { showOpsDialog = false },
			title = { Text("Neo4j Operations") },
			text = {
				Column(Modifier.fillMaxWidth().padding(16.dp)) {
					Text("Load graph as...", fontSize = 18.sp)
					Spacer(Modifier.height(12.dp))
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(
							selected = !neo4jLoadGraphIsDirected,
							onClick = { neo4jLoadGraphIsDirected = false }
						)
						Text("Undirected Graph", Modifier.padding(start = 8.dp))
					}
					Row(verticalAlignment = Alignment.CenterVertically) {
						RadioButton(
							selected = neo4jLoadGraphIsDirected,
							onClick = { neo4jLoadGraphIsDirected = true }
						)
						Text("Directed Graph", Modifier.padding(start = 8.dp))
					}
				}
			},
			confirmButton = {
				TextButton(onClick = {
					viewModel.loadNeo4j(neo4jLoadGraphIsDirected)
					showOpsDialog = false
				}) {
					Text("Load Graph")
				}
			},
			dismissButton = {
				TextButton(onClick = {
					viewModel.saveNeo4j(viewModel.graph)
					showOpsDialog = false
				}) {
					Text("Save Graph")
				}
			}
		)
	}

}
