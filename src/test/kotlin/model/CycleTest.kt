package model

import junit.framework.TestCase.assertTrue
import model.DirectedGraph.DirectedVertex
import model.UndirectedGraph.UndirectedVertex
import model.utils.CycleDetection
import org.junit.jupiter.api.BeforeEach
import space.kscience.kmath.operations.IntRing
import kotlin.test.Test
import kotlin.test.assertEquals

class CycleTest {

    @Test
    fun `Find triangle cycle in directed graph`()  {
        val cycleDetection = CycleDetection()
        val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            var index = 0
            addEdge("A", "B", index++, 5)
            addEdge("B", "C", index++, 5)
            addEdge("C", "A", index++, 5)
        }

        val list = cycleDetection.findCyclesFromGivenVertex<String, Int, Int>(graph, graph.getVertex("A") as DirectedVertex).first()
        assertEquals(graph.getEdge("C", "A"), list[0])
        assertEquals(graph.getEdge("B", "C"), list[1])
        assertEquals(graph.getEdge("A", "B"), list[2])
    }

    @Test
    fun `Find multiple cycles in directed graph`()  {
        val cycleDetection = CycleDetection()
        val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")
            addVertex("F")
            addVertex("G")
            addVertex("H")

            var index = 0

            addEdge("A", "B", index++, 5)
            addEdge("B", "C", index++, 5)
            addEdge("C", "D", index++, 5)
            addEdge("D", "E", index++, 5)
            addEdge("E", "F", index++, 5)
            addEdge("F", "A", index++, 5)

            addEdge("D", "G", index++, 5)
            addEdge("G", "H", index++, 5)
            addEdge("H", "E", index++, 5)
        }

        val list = cycleDetection.findCyclesFromGivenVertex<String, Int, Int>(graph, graph.getVertex("A") as DirectedVertex)

        assertEquals(graph.getEdge("F", "A"), list[0][0])
        assertEquals(graph.getEdge("E", "F"), list[0][1])
        assertEquals(graph.getEdge("D", "E"), list[0][2])
        assertEquals(graph.getEdge("C", "D"), list[0][3])
        assertEquals(graph.getEdge("B", "C"), list[0][4])
        assertEquals(graph.getEdge("A", "B"), list[0][5])

        assertEquals(graph.getEdge("F", "A"), list[1][0])
        assertEquals(graph.getEdge("E", "F"), list[1][1])
        assertEquals(graph.getEdge("H", "E"), list[1][2])
        assertEquals(graph.getEdge("G", "H"), list[1][3])
        assertEquals(graph.getEdge("D", "G"), list[1][4])
        assertEquals(graph.getEdge("C", "D"), list[1][5])
        assertEquals(graph.getEdge("B", "C"), list[1][6])
        assertEquals(graph.getEdge("A", "B"), list[1][7])
    }
}