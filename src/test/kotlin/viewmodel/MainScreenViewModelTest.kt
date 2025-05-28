package viewmodel

import androidx.compose.material.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import junit.framework.TestCase
import model.Constants
import model.graph.DirectedGraph
import model.graph.UndirectedGraph
import model.graph.Vertex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing
import view.MainScreenView
import view.drawerButton
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MainScreenViewModelTest {

    var testGraph: DirectedGraph<String, Int, Int> = DirectedGraph<String, Int, Int>(IntRing)

    @BeforeEach
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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `One vertex graph case`() = runComposeUiTest {
        testGraph.addVertex("A")
        val vm = MainScreenViewModel<String, Int, Int>(testGraph)
        setContent {
            MainScreenView(vm)
        }
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Vertex: A").assertExists("One vertex does not exist")
        onNodeWithTag("Algorithms").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Menus can be opened and closed`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)

        setContent {
            MainScreenView(MainScreenViewModel(testGraph))
        }
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").assertExists()
        onNodeWithTag("ShowWeightsMenuButton").assertExists()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").assertDoesNotExist()
        onNodeWithTag("ShowWeightsMenuButton").assertDoesNotExist()
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
        onNodeWithTag("FileMenu").isDisplayed()
        onNodeWithTag("FileMenu").performClick()
        onNodeWithTag("OpenMenuButton").performClick()
        onNodeWithTag("OpenDialog").assertExists()
        onNodeWithTag("JsonOpenDialogButton").assertExists()
        onNodeWithTag("Neo4jOpenDialogButton").assertExists()
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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()

        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertExists()
        onNodeWithTag("Algorithms: Louvain").assertExists()

        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertDoesNotExist()
        onNodeWithTag("Algorithms: Louvain").assertDoesNotExist()

        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").assertExists()
        onNodeWithTag("Algorithms: BellmanFord").performClick()

        onNodeWithTag("StartVertex").assertExists()
        onNodeWithTag("EndVertex").assertExists()

        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        assertEquals(Color(Constants.DEFAULT_EDGE_COLOR), vm.graphViewModel.getEdgeViewModel(firstEdge).color)
        assertEquals(Color(Constants.DEFAULT_EDGE_COLOR), vm.graphViewModel.getEdgeViewModel(secondEdge).color)
        assertEquals(Color(Constants.DEFAULT_PATH_COLOR), vm.graphViewModel.getEdgeViewModel(thirdEdge).color)
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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").performClick()

        onNodeWithTag("StartVertex").performClick()
        onNodeWithTag("StartVertex: E").performClick()

        onNodeWithTag("EndVertex").performClick()
        onNodeWithTag("EndVertex: C").performClick()

        onNodeWithTag("ApplyAlgorithm").performClick()

        assertEquals(Color(Constants.DEFAULT_EDGE_COLOR), vm.graphViewModel.getEdgeViewModel(firstEdge).color)
        assertEquals(Color(Constants.DEFAULT_PATH_COLOR), vm.graphViewModel.getEdgeViewModel(secondEdge).color)
        assertEquals(Color(Constants.DEFAULT_EDGE_COLOR), vm.graphViewModel.getEdgeViewModel(thirdEdge).color)
        assertEquals(Color(Constants.DEFAULT_PATH_COLOR), vm.graphViewModel.getEdgeViewModel(fourthEdge).color)
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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: BellmanFord").performClick()

        onNodeWithTag("StartVertex").performClick()
        onNodeWithTag("StartVertex: A").performClick()

        onNodeWithTag("EndVertex").performClick()
        onNodeWithTag("EndVertex: E").performClick()

        onNodeWithTag("ApplyAlgorithm").performClick()

        vm.graphViewModel.edges.forEach { assertEquals(Color(Constants.DEFAULT_EDGE_COLOR), it.color) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply Louvain`() = runComposeUiTest {
        val undirectedGraph = UndirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..8) addVertex(i)

            var index = 0
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) { it * 2 }

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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: Louvain").performClick()
        val list = undirectedGraph.vertices.toList()

        val firstColor = vm.graphViewModel.getVertexViewModel(list[0]).color
        val secondColor = vm.graphViewModel.getVertexViewModel(list[1]).color
        val thirdColor = vm.graphViewModel.getVertexViewModel(list[5]).color

        assertEquals(firstColor, vm.graphViewModel.getVertexViewModel(list[0]).color)
        assertEquals(firstColor, vm.graphViewModel.getVertexViewModel(list[4]).color)
        assertEquals(firstColor, vm.graphViewModel.getVertexViewModel(list[3]).color)

        assertEquals(secondColor, vm.graphViewModel.getVertexViewModel(list[1]).color)
        assertEquals(secondColor, vm.graphViewModel.getVertexViewModel(list[2]).color)

        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(list[5]).color)
        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(list[6]).color)
        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(list[7]).color)
        assertEquals(thirdColor, vm.graphViewModel.getVertexViewModel(list[8]).color)
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
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) { it * 2 }

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
        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: Kruskal").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        val list = undirectedGraph.edges.toList()

        assertNotEquals(Color(Constants.SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(list[9]).color)
        assertNotEquals(Color(Constants.SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(list[8]).color)
        assertNotEquals(Color(Constants.SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(list[5]).color)
        assertNotEquals(Color(Constants.SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(list[4]).color)
        assertNotEquals(Color(Constants.SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(list[1]).color)
        assertNotEquals(Color(Constants.SEMI_BLACK), vm.graphViewModel.getEdgeViewModel(list[0]).color)
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
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) { it * 2 }

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

        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: Tarjan").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        val colorOneComponent = vm.graphViewModel.getVertexViewModel(firstVertex).color

        val list = undirectedGraph.vertices.toList()

        assertEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[0]).color)
        assertEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[1]).color)
        assertEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[2]).color)

        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[3]).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[4]).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[5]).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[6]).color)
        assertNotEquals(colorOneComponent, vm.graphViewModel.getVertexViewModel(list[7]).color)
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Apply CycleDetection`() = runComposeUiTest {
        val firstVertex: Vertex<String>
        val undirectedGraph = DirectedGraph<String, Int, Int>(IntRing).apply {
            firstVertex = addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")
            addVertex("F")
            addVertex("G")
            addVertex("H")

            var index = 0
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) { it * 2 }

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

        onNodeWithTag("GraphMenu").isDisplayed()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ApplyAlgorithmMenuButton").performClick()
        onNodeWithTag("Algorithms").performClick()
        onNodeWithTag("Algorithms: CycleDetection").performClick()
        onNodeWithTag("ApplyAlgorithm").performClick()

        val list = undirectedGraph.edges.toList()
        val color = vm.graphViewModel.getEdgeViewModel(list[0]).color

        assertEquals(color, vm.graphViewModel.getEdgeViewModel(list[0]).color)
        assertEquals(color, vm.graphViewModel.getEdgeViewModel(list[1]).color)
        assertEquals(color, vm.graphViewModel.getEdgeViewModel(list[2]).color)

    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `Alert dialog works correctly`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 1, 1)
        var vm = MainScreenViewModel(testGraph)
        vm.exceptionMessage = "message"
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
            val coroutine = rememberCoroutineScope()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            var title by remember { mutableStateOf("A button") }
            drawerButton(title, Icons.Default.Check, "TestButton", coroutine, drawerState) {
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
    fun `Test weights`() = runComposeUiTest {
        testGraph.addVertex("A")
        testGraph.addVertex("B")
        testGraph.addEdge("A", "B", 0, 5)


        var vm = MainScreenViewModel(testGraph)
        setContent {
            MainScreenView(vm)
        }
        onNodeWithTag("EdgeLabel: 0").assertDoesNotExist()
        Assertions.assertFalse(vm.showEdgesWeights.value)
        onNodeWithTag("GraphMenu").assertExists()
        onNodeWithTag("GraphMenu").performClick()
        onNodeWithTag("ShowWeightsMenuButton").assertExists()
        onNodeWithTag("ShowWeightsMenuButton").performClick()
        Assertions.assertTrue(vm.showEdgesWeights.value)
        onNodeWithTag("EdgeLabel: 0").assertExists()
        onNodeWithTag("EdgeLabel: 0").assertTextEquals("5")
    }


}