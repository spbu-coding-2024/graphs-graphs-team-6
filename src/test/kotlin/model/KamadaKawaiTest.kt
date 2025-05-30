package model

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.RepeatedTest
import view.MainScreenView
import viewmodel.MainScreenViewModel
import kotlin.random.Random
import kotlin.random.nextInt

class KamadaKawaiTest {
    val maxVertices = 12

    @OptIn(ExperimentalTestApi::class)
    @RepeatedTest(3)
    fun `KamadaKawai algorithm converges on random graph when vertices are all appear on screen`() = runComposeUiTest {
        val randomGraph = RandomUndirectedIntGraph.get(maxVertices)
        val vm = MainScreenViewModel(randomGraph)
        setContent {
            MainScreenView(vm)
        }
        vm.drawGraph()

    }

    @OptIn(ExperimentalTestApi::class)
    @RepeatedTest(3)
    fun `KamadaKawai algorithm converges on random graph when vertices are not shown on screen`() = runComposeUiTest {
        val randomGraph = RandomUndirectedIntGraph.get(maxVertices)
        val vm = MainScreenViewModel(randomGraph)
        for (vertex in vm.graphViewModel.vertices) {
            vertex.x = -Random.nextInt(1000, 10000).dp
            vertex.y = -Random.nextInt(1000, 10000).dp
        }
        setContent {
            MainScreenView(vm)
        }

        vm.drawGraph()

    }
}
