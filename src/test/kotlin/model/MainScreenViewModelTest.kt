package model

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

import space.kscience.kmath.operations.IntRing
import view.MainScreenView
import viewmodel.MainScreenViewModel

class MainScreenViewModelTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Empty graph case`() = runComposeUiTest {
        val emptyGraph = DirectedGraph<String, Int, Int>(IntRing)
        val vm = MainScreenViewModel<String, Int, Int>(emptyGraph)
        setContent {
            MainScreenView(vm)
        }
        onNodeWithTag("MainButton").assertExists("Main button does not exist")
        onNodeWithTag("OpenButton").assertIsNotDisplayed()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
    }
}
