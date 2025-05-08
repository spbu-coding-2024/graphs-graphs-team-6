package model

import model.utils.DijkstraPathCalculator
import org.junit.jupiter.api.*
import space.kscience.kmath.operations.IntRing
import kotlin.test.assertEquals

@DisplayName("Dijkstra path calculator")
class DijkstraPathCalculatorTest {
    private lateinit var digraph: DirectedGraph<Int, Int, Int>
    private val algorithm = DijkstraPathCalculator()

    @BeforeEach
    fun setup() {
        digraph = DirectedGraph(IntRing)
    }

    @Test
    @DisplayName("Empty graph")
    fun emptyGraph() {
        val (predeccor, distance) = algorithm.runOn(digraph, 0)
        assertEquals(0, predeccor.size)
        assertEquals(1, distance.size)
        assertEquals(0, distance[0])
    }
}