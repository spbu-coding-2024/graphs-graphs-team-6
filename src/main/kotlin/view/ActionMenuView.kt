package view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.utils.SSSPCalculator
import viewmodel.ColorUtils
import viewmodel.MainScreenViewModel



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <V, K, W: Comparable<W>>actionMenuView(actionWindowVisibility: Boolean, viewModel: MainScreenViewModel<V, K, W>) {
    var currentAlgorithm: Int = Algorithm.BellmanFord.ordinal
    val algorithms = returnArrayOfAlgorithmLabels()
    var menuIsExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = actionWindowVisibility,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .width(300.dp)
                .height(300.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
        ) {
            ExposedDropdownMenuBox(
                expanded = menuIsExpanded,
                onExpandedChange = {
                    menuIsExpanded = !menuIsExpanded
                },
            ) {
                TextField(
                    value = algorithms[currentAlgorithm],
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuIsExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = menuIsExpanded,
                    onDismissRequest = { menuIsExpanded = false },
                ) {
                    algorithms.forEachIndexed { ordinal, string ->
                        DropdownMenuItem(
                            onClick = {
                                currentAlgorithm = ordinal
                                menuIsExpanded = false
                            }
                        ) {
                            Text(text = string)
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .padding(5.dp),
                onClick = {
                    applyAlgorithm(currentAlgorithm, viewModel)
                }
            ) {
                Icon(Icons.Default.Check, "")
            }
        }
    }
}
fun returnArrayOfAlgorithmLabels(): Array<String> {
    return Array<String>(Algorithm.entries.size) {
        when(it) {
            Algorithm.BellmanFord.ordinal -> "Bellman-Ford"
            Algorithm.Tarjan.ordinal -> "Tarjan Strong Connected Component"
            else -> error("No string for enum")
        }
    }
}

enum class Algorithm {
    Tarjan,
    BellmanFord
}

fun <V, K, W: Comparable<W>>applyAlgorithm(algoNum: Int, viewModel: MainScreenViewModel<V, K, W>) {
    when (algoNum) {
        Algorithm.Tarjan.ordinal -> viewModel.calculateSCC()

        Algorithm.BellmanFord.ordinal -> {
            val (predecessors, distance) = SSSPCalculator.bellmanFordAlgorithm(
                viewModel.graph,
                viewModel.graph.vertices.first().element
            )
            val path = SSSPCalculator.constructPath(predecessors, viewModel.graph.vertices.last().element)
                .map {viewModel.graphViewModel.getEdgeViewModel(it)}
            ColorUtils.applyOneColor(path, Color.Red)
        }
    }
}
