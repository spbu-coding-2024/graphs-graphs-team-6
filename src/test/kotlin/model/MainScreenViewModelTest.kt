package model

import androidx.compose.material.Icon
import kotlin.math.max
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import model.Constants.DEFAULT_EDGE_COLOR
import model.Constants.DEFAULT_PATH_COLOR
import model.Constants.SEMI_BLACK
import org.junit.Rule
import space.kscience.kmath.expressions.DSRing
import space.kscience.kmath.nd.IntRingND
import space.kscience.kmath.nd.RingND
import space.kscience.kmath.operations.Group
import kotlin.test.Test

import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.MutableBufferFactory
import view.LouvainAlertDialog
import view.MainScreenView
import view.drawerButton
import viewmodel.MainScreenViewModel
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MainScreenViewModelTest {

    var testGraph: DirectedGraph<String, Int, Int> = DirectedGraph<String, Int, Int>(IntRing)

    @BeforeTest
    fun before() {
        if (testGraph.vertices.isNotEmpty()) {
            testGraph = DirectedGraph<String, Int, Int>(IntRing)
        }
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Empty graph case`() = runComposeUiTest {

        var vm = MainScreenViewModel<String, Int, Int>(testGraph)
        setContent {
            MainScreenView(vm)
        }
        onNodeWithTag("MainButton").assertExists("Main button does not exist")
        onNodeWithTag("OpenButton").assertIsNotDisplayed()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `One vertex graph case`() = runComposeUiTest {
        testGraph.addVertex("A")
        val vm = MainScreenViewModel<String, Int, Int>(testGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("Vertex: A").assertExists("One vertex does not exist")
        onNodeWithTag("MainButton").assertExists("Main button does not exist")
        onNodeWithTag("OpenButton").assertIsNotDisplayed()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Main drawer can be opened and closed`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)

        setContent {
            MainScreenView(MainScreenViewModel(testGraph))
        }
        onNodeWithTag("MainButton").assertHasClickAction()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").assertTextEquals("Action")
        onNodeWithTag("ActionButton").assertHasClickAction()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Open button is working correctly`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)

        setContent {
            MainScreenView(MainScreenViewModel(testGraph))
        }
        onNodeWithTag("MainButton").assertHasClickAction()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").assertTextEquals("Action")
        onNodeWithTag("ActionButton").assertHasClickAction()
        onNodeWithTag("OpenButton").performClick()
        onNodeWithTag("OpenButton").assertIsNotDisplayed()
        onNodeWithTag("ActionButton").assertIsNotDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Action can be open and closed`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)

        setContent {
            MainScreenView(MainScreenViewModel(testGraph))
        }
        onNodeWithTag("MainButton").assertHasClickAction()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").assertTextEquals("Action")
        onNodeWithTag("ActionButton").assertHasClickAction()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").assertIsDisplayed()
        onNodeWithTag("ApplyAlgorithm").assertIsDisplayed()
        onNodeWithTag("MainButton").assertHasClickAction()
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("Algorithms").assertDoesNotExist()
        onNodeWithTag("ApplyAlgorithm").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Action expandable`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)

        setContent {
            MainScreenView(MainScreenViewModel(testGraph))
        }
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertExists()
        onNodeWithTag("Algorithms: Louvain").assertExists()

        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertDoesNotExist()
        onNodeWithTag("Algorithms: Louvain").assertDoesNotExist()

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("Algorithms").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Expanded menus force close`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)

        setContent {
            MainScreenView(MainScreenViewModel(testGraph))
        }
        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertExists()
        onNodeWithTag("Algorithms: BellmanFord").performClick()

        onNodeWithTag("StartVertex").assertExists()
        onNodeWithTag("EndVertex").assertExists()

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertDoesNotExist()
        onNodeWithTag("StartVertex").assertDoesNotExist()
        onNodeWithTag("EndVertex").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply BellmanFord`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addVertex("C")
        val firstEdge = testGraph.addEdge("A", "B", 0, 5)
        val secondEdge = testGraph.addEdge("B", "C", 1, 6)
        val thirdEdge = testGraph.addEdge("A", "C", 2, 1)
        val vm = MainScreenViewModel(testGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        assertEquals(Color(DEFAULT_EDGE_COLOR),vm.graphViewModel.getEdgeViewModel(firstEdge).color)
        assertEquals(Color(DEFAULT_EDGE_COLOR),vm.graphViewModel.getEdgeViewModel(secondEdge).color)
        assertEquals(Color(DEFAULT_PATH_COLOR),vm.graphViewModel.getEdgeViewModel(thirdEdge).color)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply BellmanFord with different start and end vertex`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addVertex("C")
        testGraph.addVertex("D")
        testGraph.addVertex("E")
        val firstEdge = testGraph.addEdge("A", "B", 0, 5)
        val secondEdge = testGraph.addEdge("B", "C", 1, 6)
        val thirdEdge = testGraph.addEdge("C", "D", 2, 1)
        val fourthEdge = testGraph.addEdge("E", "B", 3, 1)
        val vm = MainScreenViewModel(testGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").performClick()

        onNodeWithTag("StartVertex").performClick()
        onNodeWithTag("StartVertex: E").performClick()

        onNodeWithTag("EndVertex").performClick()
        onNodeWithTag("EndVertex: C").performClick()

        onNodeWithTag("ApplyAlgorithm").performClick()

        assertEquals(Color(DEFAULT_EDGE_COLOR), vm.graphViewModel.getEdgeViewModel(firstEdge).color)
        assertEquals(Color(DEFAULT_PATH_COLOR), vm.graphViewModel.getEdgeViewModel(secondEdge).color)
        assertEquals(Color(DEFAULT_EDGE_COLOR), vm.graphViewModel.getEdgeViewModel(thirdEdge).color)
        assertEquals(Color(DEFAULT_PATH_COLOR), vm.graphViewModel.getEdgeViewModel(fourthEdge).color)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply BellmanFord will color edges if no such path`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addVertex("C")
        testGraph.addVertex("D")
        testGraph.addVertex("E")
        testGraph.addEdge("A", "B", 0, 5)
        testGraph.addEdge("B", "C", 1, 6)
        testGraph.addEdge("C", "D", 2, 1)
        testGraph.addEdge("E", "B", 3, 1)


        val vm = MainScreenViewModel(testGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").performClick()

        onNodeWithTag("StartVertex").performClick()
        onNodeWithTag("StartVertex: A").performClick()

        onNodeWithTag("EndVertex").performClick()
        onNodeWithTag("EndVertex: E").performClick()

        onNodeWithTag("ApplyAlgorithm").performClick()

        vm.graphViewModel.edges.forEach { assertEquals(Color(DEFAULT_EDGE_COLOR), it.color) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply Louvain`() = runComposeUiTest {
        val undirectedGraph = UndirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..8) addVertex(i)

            var index = 0
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) {it * 2}

            addEdge(0, 1, index, weight[index]); index++
            addEdge(4, 1, index, weight[index]); index++
            addEdge(4, 0, index, weight[index]); index++

            addEdge(4, 3, index, weight[index]); index++
            addEdge(3, 2, index, weight[index]); index++
            addEdge(2, 1, index, weight[index]); index++

            addEdge(4, 5, index, weight[index]); index++

            addEdge(5, 8, index, weight[index]); index++
            addEdge(8, 7, index, weight[index]); index++
            addEdge(6, 7, index, weight[index]); index++
            addEdge(6, 8, index, weight[index]); index++
            addEdge(6, 5, index, weight[index]); index++
        }

        val vm = MainScreenViewModel(undirectedGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: Louvain").performClick()

        val firstColor = vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(0)).color
        val secondColor = vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(1)).color
        val thirdColor = vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(5)).color

        assertEquals(firstColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(0)).color)
        assertEquals(firstColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(4)).color)
        assertEquals(firstColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(3)).color)

        assertEquals(secondColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(1)).color)
        assertEquals(secondColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(2)).color)

        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(5)).color)
        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(6)).color)
        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(7)).color)
        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex(8)).color)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply Kruskal`() = runComposeUiTest {
        val undirectedGraph = UndirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")
            addVertex("F")
            addVertex("G")
            addVertex("H")

            var index = 0
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) {it * 2}

            addEdge("A", "B", index, weight[index]); index++
            addEdge("B", "C", index, weight[index]); index++
            addEdge("C", "A", index, weight[index]); index++
            addEdge("C", "C", index, weight[index]); index++

            addEdge("C", "F", index, weight[index]); index++

            addEdge("D", "E", index, weight[index]); index++
            addEdge("E", "F", index, weight[index]); index++
            addEdge("F", "D", index, weight[index]); index++


            addEdge("H", "D", index, weight[index]); index++

            addEdge("G", "H", index, weight[index]); index++
        }

        val vm = MainScreenViewModel(undirectedGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: Kruskal").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        assertNotEquals(Color(SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(undirectedGraph.getEdge("G", "H")).color)
        assertNotEquals(Color(SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(undirectedGraph.getEdge("H", "D")).color)
        assertNotEquals(Color(SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(undirectedGraph.getEdge("D", "E")).color)
        assertNotEquals(Color(SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(undirectedGraph.getEdge("C", "F")).color)
        assertNotEquals(Color(SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(undirectedGraph.getEdge("B", "C")).color)
        assertNotEquals(Color(SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(undirectedGraph.getEdge("A", "B")).color)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply Tarjan`() = runComposeUiTest {
        val firstVertex: Vertex<String>
        val undirectedGraph = UndirectedGraph<String, Int, Int>(IntRing).apply {
            firstVertex = addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")
            addVertex("F")
            addVertex("G")
            addVertex("H")

            var index = 0
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) {it * 2}

            addEdge("A", "B", index, weight[index]); index++
            addEdge("B", "C", index, weight[index]); index++
            addEdge("C", "A", index, weight[index]); index++
            addEdge("C", "C", index, weight[index]); index++


            addEdge("D", "E", index, weight[index]); index++
            addEdge("E", "F", index, weight[index]); index++
            addEdge("F", "D", index, weight[index]); index++

            addEdge("H", "D", index, weight[index]); index++

            addEdge("G", "H", index, weight[index]); index++
        }

        val vm = MainScreenViewModel(undirectedGraph)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("MainButton").performClick()
        onNodeWithTag("ActionButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: Tarjan").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        val colorOneComponent = vm.graphViewModel.getVertexViewModel(firstVertex).color

        assertEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("A")).color)
        assertEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("B")).color)
        assertEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("C")).color)

        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("D")).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("E")).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("F")).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("G")).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(undirectedGraph.getVertex("H")).color)
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Louvain Alert dialog works correctly`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 1, 1)
        var vm = MainScreenViewModel(testGraph)
        vm.showIncompatibleWeightTypeDialog = true
        setContent {
            MainScreenView(vm)
        }
        onNodeWithTag("AlertDialog").assertExists()
        onNodeWithTag("AlertDialogButton").performClick()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `drawerButton reacts on click`() = runComposeUiTest {
        setContent {
            var title by remember { mutableStateOf("A button") }
            drawerButton(title, Icons.Default.Check, "TestButton") {
                title = "A pressed button"
            }
        }
        onNodeWithTag("TestButton").assertExists()
        onNodeWithTag("TestButton").assertTextEquals("A button")
        onNodeWithTag("TestButton").performClick()
        onNodeWithTag("TestButton").assertTextEquals("A pressed button")
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `test Weight check box`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        val edge = testGraph.addEdge("A", "B", 0, 5)


        var vm = MainScreenViewModel(testGraph)
        val edgeVM = vm.graphViewModel.getEdgeViewModel(edge)
        setContent {
            MainScreenView(vm)
        }

        onNodeWithTag("WeightCheckBox").assertExists()
        onNodeWithTag("EdgeLabel: 0").assertDoesNotExist()
        assertFalse(vm.showEdgesWeights)

        onNodeWithTag("WeightCheckBox").performClick()
        assertTrue(vm.showEdgesWeights)
        onNodeWithTag("EdgeLabel: 0").assertExists()
        onNodeWithTag("EdgeLabel: 0").assertTextEquals("5")
    }
}
