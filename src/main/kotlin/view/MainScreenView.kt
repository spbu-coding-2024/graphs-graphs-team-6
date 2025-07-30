package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
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
import kotlinx.coroutines.CoroutineScope
import model.neo4j.GraphService
import model.sqlite.SQLiteManager
import viewmodel.Neo4jAction

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>> MainScreenView(viewModel: MainScreenViewModel<V, K, W>) {
        GraphView(viewModel.graphViewModel)
        if (viewModel.graphViewModel.vertices.isNotEmpty()) actionMenuView(
                viewModel.actionWindowVisibility.value,
                viewModel
        )
        if (viewModel.aboutDialog.value) aboutDialog(viewModel)
        openMenu(viewModel)
        saveMenu(viewModel)
        neo4jMenu(viewModel)
        loadSQLiteMenu(viewModel)
        saveSQLiteMenu(viewModel)
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> aboutDialog(viewModel: MainScreenViewModel<V, K, W>) {
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
                                        "Choose \"Graph\" menu and then \"Apply algorithms\" to try some " +
                                        "algorithms on a graph, " +
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
fun <V : Any, K : Any, W : Comparable<W>> saveMenu(
        viewModel: MainScreenViewModel<V, K, W>
) {
        if (viewModel.saveDialogState.value) {
                AlertDialog(
                        modifier = Modifier.testTag("SaveDialog"),
                        onDismissRequest = { viewModel.saveDialogState.value = false },
                        title = { Text("Select destination to save") },
                        text = {
                                Column {
                                        Spacer(Modifier.height(8.dp))

                                        Button(
                                                modifier = Modifier.testTag("Neo4jSaveDialogButton"),
                                                onClick = {
                                                        viewModel.saveDialogState.value = false
                                                        if (GraphService.sessionFactory == null) {
                                                                viewModel.showNeo4jDialog = true
                                                        }
                                                        viewModel.pendingNeo4jAction = Neo4jAction.SAVE
                                                }
                                        ) {
                                                Text("Neo4j")
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Button(
                                                modifier = Modifier.testTag("JsonSaveDialogButton"),
                                                onClick = {
                                                        viewModel.saveDialogState.value = false
                                                        viewModel.saveJSON()
                                                }
                                        ) {
                                                Text("JSON")
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        Button(
                                                modifier = Modifier.testTag("SQLiteSaveDialogButton"),
                                                onClick = {
                                                        viewModel.saveDialogState.value = false
                                                        viewModel.showSaveSQLiteMenu.value = true
                                                }
                                        ) {
                                                Text("SQLite")
                                        }
                                }
                        },
                        buttons = {}
                )
        }
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> openMenu(
        viewModel: MainScreenViewModel<V, K, W>
) {
        if (viewModel.showDbSelectDialog.value) {
                AlertDialog(
                        modifier = Modifier.testTag("OpenDialog"),
                        onDismissRequest = { viewModel.showDbSelectDialog.value = false },
                        title = { Text("Select source to open") },
                        text = {
                                Column {
                                        Spacer(Modifier.height(8.dp))
                                        Button(
                                                modifier = Modifier.testTag("Neo4jOpenDialogButton"),
                                                onClick = {
                                                        viewModel.showDbSelectDialog.value = false
                                                        viewModel.pendingNeo4jAction = Neo4jAction.LOAD
                                                        if (GraphService.sessionFactory == null)
                                                                viewModel.showNeo4jDialog = true

                                                        if (GraphService.sessionFactory != null)
                                                                viewModel.showNeo4jOpsDialog = true

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
                                        Spacer(Modifier.height(8.dp))
                                        Button(
                                                modifier = Modifier.testTag("SQLiteOpenDialogButton"),
                                                onClick = {
                                                        viewModel.showDbSelectDialog.value = false
                                                        viewModel.showLoadSQLiteMenu.value = true
                                                }) {
                                                Text("SQLite")
                                        }
                                }
                        },
                        buttons = {}
                )
        }
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> neo4jMenu(
        viewModel: MainScreenViewModel<V, K, W>,
) {
        var neo4jUri by remember { mutableStateOf("") }
        var neo4jUser by remember { mutableStateOf("") }
        var neo4jPassword by remember { mutableStateOf("") }
        var neo4jLoadGraphIsDirected = remember { mutableStateOf(false) }

        if (viewModel.showNeo4jDialog && GraphService.sessionFactory == null) {
                AlertDialog(
                        onDismissRequest = { viewModel.showNeo4jDialog = false },
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
                                        viewModel.showNeo4jDialog = false
                                        when (viewModel.pendingNeo4jAction) {
                                                Neo4jAction.LOAD -> viewModel.showNeo4jOpsDialog = true
                                                Neo4jAction.SAVE -> GraphService.saveGraph(viewModel.graph)
                                                else -> {}
                                        }
                                }) {
                                        Text("Connect")
                                }
                        },
                        dismissButton = {
                                TextButton(onClick = { viewModel.showNeo4jDialog = false }) {
                                        Text("Cancel")
                                }
                        }
                )
        }
        opsDialog(viewModel, neo4jLoadGraphIsDirected)
        neo4jConnectionExceptionDialog(viewModel)
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> opsDialog(
        viewModel: MainScreenViewModel<V, K, W>,
        neo4jLoadGraphIsDirected: MutableState<Boolean>
) {
        if (viewModel.showNeo4jOpsDialog) {
                AlertDialog(
                        onDismissRequest = { viewModel.showNeo4jOpsDialog = false },
                        title = { Text("Neo4j Load Option") },
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
                                        viewModel.showNeo4jOpsDialog = false
                                }) {
                                        Text("Load Graph")
                                }
                        }
                )
        }
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> neo4jConnectionExceptionDialog(viewModel: MainScreenViewModel<V, K, W>) {
        if (viewModel.showNeo4jConnectionFailedDialog) {
                AlertDialog(
                        onDismissRequest = {
                                viewModel.showNeo4jConnectionFailedDialog = false
                        },
                        title = { Text("Connection Failed!") },
                        text = {
                                Text(
                                        "Cannot connect to Neo4j database. Check if credentials are not correct." +
                                                "Exception message:" +
                                                "\n${viewModel.exceptionMessage}"
                                )
                        },
                        confirmButton = {
                                TextButton(onClick = {
                                        viewModel.showNeo4jConnectionFailedDialog = false
                                }) {
                                        Text("ОК")
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>>loadSQLiteMenu(
        viewModel: MainScreenViewModel<V, K, W>
)
{
        val graphList = remember { mutableStateOf<List<String>>(emptyList()) }

        if (viewModel.showLoadSQLiteMenu.value) {
                graphList.value = SQLiteManager.getGraphNames(SQLiteManager.createConnection())
                if (graphList.value.isEmpty()){
                        viewModel.exceptionMessage = "Cannot load graph names."
                        viewModel.showLoadSQLiteMenu.value = false
                }
                else {
                        val graphName = remember { mutableStateOf(graphList.value[0]) }
                        AlertDialog(
                                onDismissRequest = { viewModel.showLoadSQLiteMenu.value = false },
                                title = { Text("Select Graph") },
                                text = {
                                        var selectedOption by remember { mutableStateOf(graphName.value) }
                                        var expanded by remember { mutableStateOf(false) }
                                        ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                content = {
                                                        TextField(
                                                                value = selectedOption,
                                                                onValueChange = {selectedOption = graphName.value},
                                                                readOnly = true,
                                                                label = { Text("Choose graph") },
                                                                trailingIcon = {
                                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                                                expanded = expanded
                                                                        )
                                                                }
                                                        )
                                                        ExposedDropdownMenu(
                                                                expanded = expanded,
                                                                onDismissRequest = { expanded = false }
                                                        ) {
                                                                graphList.value.forEach { selectionOption ->
                                                                        DropdownMenuItem(
                                                                                content = { Text(selectionOption) },
                                                                                onClick = {
                                                                                        selectedOption = selectionOption
                                                                                        expanded = false
                                                                                }
                                                                        )
                                                                }
                                                        }
                                                          },
                                                onExpandedChange = {expanded = !expanded}
                                        )
                                },
                                confirmButton = {
                                        TextButton(onClick = {
                                                viewModel.showLoadSQLiteMenu.value = false
                                                viewModel.loadSQLite(graphName.value)
                                        }) {
                                                Text("Ok")
                                        }
                                },
                                dismissButton = {
                                        TextButton(onClick = { viewModel.showLoadSQLiteMenu.value = false }) {
                                                Text("Cancel")
                                        }
                                }
                        )
                }
        }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>>saveSQLiteMenu(
        viewModel: MainScreenViewModel<V, K, W>
)
{
        var graphName by remember { mutableStateOf("") }
        if (viewModel.showSaveSQLiteMenu.value) {
                        AlertDialog(
                                onDismissRequest = { viewModel.showSaveSQLiteMenu.value = false },
                                title = { Text("Save graph...") },
                                text = {

                                        OutlinedTextField(
                                                value = graphName,
                                                onValueChange = { graphName = it },
                                                label = { Text("Enter graph name") },
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                },
                                confirmButton = {
                                        TextButton(onClick = {
                                        viewModel.showSaveSQLiteMenu.value = false
                                        viewModel.saveSQLite(graphName)
                                        }) {
                                                Text("Ok")
                                        }
                                },
                                dismissButton = {
                                        TextButton(onClick = { viewModel.showSaveSQLiteMenu.value = false }) {
                                                Text("Cancel")
                                        }
                                }
                        )

        }
}
