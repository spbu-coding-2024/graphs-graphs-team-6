package model

import model.utils.DijkstraPathCalculator
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DynamicTest.dynamicTest
import space.kscience.kmath.operations.IntRing
import kotlin.Int.Companion.MAX_VALUE
import kotlin.test.assertEquals

private const val DIRECTED = "directed"
private const val UNDIRECTED = "undirected"

@DisplayName("Dijkstra path calculator")
class DijkstraPathCalculatorTest {
    private val graph: MutableMap<String, Graph<String, Int, Int>> = mutableMapOf(
        DIRECTED to DirectedGraph(IntRing),
        UNDIRECTED to UndirectedGraph(IntRing)
    )
    private val algorithm = DijkstraPathCalculator()
    private val graphType = listOf(DIRECTED, UNDIRECTED)

    @BeforeEach
    fun setup() {
        graph[DIRECTED] = DirectedGraph(IntRing)
        graph[UNDIRECTED] = UndirectedGraph(IntRing)
    }

    @TestFactory
    @DisplayName("Empty graph")
    fun emptyGraph() {
        graphType.map {
            dynamicTest("Empty $it graph") {
                if (graph[it] != null) {
                    val (predecessor, distance) = algorithm.runOn(graph[it] as Graph, "A")
                    assertEquals(0, predecessor.size)
                    assertEquals(1, distance.size)
                    assertEquals(0, distance["A"])
                }
            }
        }
    }

    @TestFactory
    @DisplayName("Single vertex graph")
    fun singleVertexGraph() {
        graphType.map {
            dynamicTest("Single vertex $it graph") {
                if (graph[it] != null) {
                    graph[it]?.addVertex("A")
                    val (predecessor, distance) = algorithm.runOn(graph[it] as Graph, "A")
                    assertEquals(0, predecessor.size)
                    assertEquals(1, distance.size)
                    assertEquals(0, distance["A"])
                }
            }
        }
    }

    @TestFactory
    @DisplayName("Single edge graph")
    fun singleEdgeGraph() {
        graphType.map {
            dynamicTest("Single edge $it graph") {
                if (graph[it] != null) {
                    graph[it]?.apply {
                        addVertex("A")
                        addVertex("B")
                        addEdge("A", "B", 1, 1)
                    }
                    var distance = algorithm.runOn(graph[it] as Graph, "A").second
                    assertEquals(2, distance.size)
                    assertEquals(0, distance["A"])
                    assertEquals(1, distance["B"])

                    distance = algorithm.runOn(graph[it] as Graph, "B").second
                    assertEquals(2, distance.size)
                    when (graph[it]) {
                        is UndirectedGraph -> {
                            assertEquals(0, distance["A"])
                            assertEquals(1, distance["B"])
                        }
                        is DirectedGraph -> {
                            assertEquals(MAX_VALUE, distance["A"])
                            assertEquals(0, distance["B"])
                        }
                    }
                }
            }
        }
    }

    @TestFactory
    @DisplayName("Single cycle graph")
    fun singleCycleGraph() {
        graphType.map {
            dynamicTest("Single cycle $it graph") {
                if (graph[it] != null) {
                    var index = 0
                    graph[it]?.apply {
                        addVertex("A")
                        addVertex("B")
                        addVertex("C")
                        addEdge("A", "B", index++, 1)
                        addEdge("B", "C", index++, 1)
                        addEdge("C", "A", index, 1)
                    }
                    val distance = algorithm.runOn(graph[it] as Graph, "A").second
                    assertEquals(3, distance.size)
                    assertEquals(0, distance["A"])
                    assertEquals(1, distance["B"])
                    when (graph[it]) {
                        is UndirectedGraph -> assertEquals(1, distance["C"])
                        is DirectedGraph -> assertEquals(2, distance["C"])
                    }
                }
            }
        }
    }

    @TestFactory
    @DisplayName("Loop")
    fun loopGraph() {
        graphType.map {
            dynamicTest("Loop $it graph") {
                if (graph[it] != null) {
                    var index = 0
                    graph[it]?.apply {
                        addVertex("A")
                        addVertex("B")
                        addVertex("C")
                        addEdge("A", "A", index++, 1)
                        addEdge("A", "B", index++, 1)
                        addEdge("B", "C", index++, 1)
                        addEdge("C", "A", index, 1)
                    }
                    val distance = algorithm.runOn(graph[it] as Graph, "A").second
                    assertEquals(3, distance.size)
                    assertEquals(0, distance["A"])
                    assertEquals(1, distance["B"])
                    when (graph[it]) {
                        is UndirectedGraph -> assertEquals(1, distance["C"])
                        is DirectedGraph -> assertEquals(2, distance["C"])
                    }
                }
            }
        }
    }

    @TestFactory
    @DisplayName("Complex graph")
    fun complexGraph() {
        graphType.map {current ->
            dynamicTest("Complex $current graph") {
                if (graph[current] != null) {
                    var index = 0
                    graph[current]?.apply {
                        "ABCDEFGHI".forEach {char -> addVertex(char.toString()) }
                        addEdge("A", "A", index++, 1)
                        addEdge("A", "B", index++, 1)
                        addEdge("B", "C", index++, 1)
                        addEdge("C", "A", index++, 1)

                        addEdge("A", "A", index++, 9)
                        addEdge("A", "B", index++, 9)
                        addEdge("B", "C", index++, 9)
                        addEdge("C", "A", index++, 9)

                        addEdge("D", "E", index++, 1)
                        addEdge("E", "F", index++, 1)
                        addEdge("F", "G", index++, 1)
                        addEdge("I", "E", index++, 1)
                        addEdge("I", "D", index++, 1)
                        addEdge("I", "H", index++, 1)
                        addEdge("F", "G", index++, 1)
                        addEdge("G", "H", index, 1)
                    }
                    var distance = algorithm.runOn(graph[current] as Graph, "A").second
                    assertEquals(0, distance["A"])
                    assertEquals(1, distance["B"])
                    "DEFGHI".forEach {char -> assertEquals(MAX_VALUE, distance[char.toString()]) }
                    when (graph[current]) {
                        is UndirectedGraph -> assertEquals(1, distance["C"])
                        is DirectedGraph -> assertEquals(2, distance["C"])
                    }
                    distance = algorithm.runOn(graph[current] as Graph, "D").second
                    assertEquals(0, distance["D"])
                    assertEquals(1, distance["E"])
                    "ABC".forEach {char -> assertEquals(MAX_VALUE, distance[char.toString()]) }
                    when (graph[current]) {
                        is UndirectedGraph -> {
                            assertEquals(2, distance["F"])
                            assertEquals(3, distance["G"])
                            assertEquals(2, distance["H"])
                            assertEquals(1, distance["I"])
                        }
                        is DirectedGraph -> {
                            assertEquals(2, distance["F"])
                            assertEquals(3, distance["G"])
                            assertEquals(4, distance["H"])
                            assertEquals(MAX_VALUE, distance["I"])
                        }
                    }
                }
            }
        }
    }
}
