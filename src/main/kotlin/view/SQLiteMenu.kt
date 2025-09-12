package view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import model.sqlite.createConnection
import model.sqlite.getGraphNames
import viewmodel.MainScreenViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V : Any, K : Any, W : Comparable<W>> loadSQLiteMenu(
    viewModel: MainScreenViewModel<V, K, W>
) {
    val graphList = remember { mutableStateOf<List<String>>(emptyList()) }

    if (viewModel.showLoadSQLiteMenu.value) {
        graphList.value = getGraphNames(createConnection())
        if (graphList.value.isEmpty()) {
            viewModel.exceptionMessage = "Cannot load graph names."
            viewModel.showLoadSQLiteMenu.value = false
        } else {
            val graphName = remember { mutableStateOf(graphList.value[0]) }
            AlertDialog(onDismissRequest = { viewModel.showLoadSQLiteMenu.value = false },
                title = { Text("Select Graph") },
                text = {
                    var selectedOption by remember { mutableStateOf(graphName.value) }
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, content = {
                        TextField(value = selectedOption,
                            onValueChange = { selectedOption = graphName.value },
                            readOnly = true,
                            label = { Text("Choose graph") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            })
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            graphList.value.forEach { selectionOption ->
                                DropdownMenuItem(content = { Text(selectionOption) }, onClick = {
                                    selectedOption = selectionOption
                                    expanded = false
                                })
                            }
                        }
                    }, onExpandedChange = { expanded = !expanded })
                },
                confirmButton = {
                    ConfirmButton(
                        onClick = { viewModel.loadSQLite(graphName.value) },
                        showMenuVariable = viewModel.showLoadSQLiteMenu
                    )
                },
                dismissButton = {
                    DismissButton(
                        showMenuVariable = viewModel.showLoadSQLiteMenu
                    )
                })
        }
    }
}

@Composable
fun <V : Any, K : Any, W : Comparable<W>> saveSQLiteMenu(
    viewModel: MainScreenViewModel<V, K, W>
) {
    var graphName by remember { mutableStateOf("") }
    if (viewModel.showSaveSQLiteMenu.value) {
        AlertDialog(onDismissRequest = { viewModel.showSaveSQLiteMenu.value = false },
            title = { Text("Save graph...") },
            text = {
                OutlinedTextField(value = graphName,
                    onValueChange = { graphName = it },
                    label = { Text("Enter graph name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                ConfirmButton(
                    onClick = { viewModel.saveSQLite(graphName) }, showMenuVariable = viewModel.showSaveSQLiteMenu
                )
            },
            dismissButton = {
                DismissButton(
                    showMenuVariable = viewModel.showSaveSQLiteMenu
                )
            })

    }
}
