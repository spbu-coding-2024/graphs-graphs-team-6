package viewmodel

import kotlin.test.Test

import androidx.compose.ui.test.*
import model.DirectedGraph
import space.kscience.kmath.operations.IntRing
import view.MainScreenView

class MainScreenViewModelTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Empty graph case`() = runComposeUiTest {
        val emptyGraph = DirectedGraph<String, Int, Int>(IntRing)
        val graphViewModel = GraphViewModel<String, Int, Int>(emptyGraph)
        val vm = MainScreenViewModel<String, Int, Int>(emptyGraph, graphViewModel)
        setContent {
            MainScreenView(vm)
        }
        onNodeWithTag("MainButton").assertExists("Main button does not exist")
        onNodeWithTag("OpenButton").assertIsNotDisplayed()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
    }
}