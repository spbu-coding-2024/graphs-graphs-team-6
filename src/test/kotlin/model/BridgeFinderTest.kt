package model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing

@DisplayName("Tests for BridgeFinder")
class BridgeFinderTest {
    private lateinit var graph: UndirectedGraph<Int, Int, Int>
    private var algorithm: BridgeFinder = BridgeFinder()

    @BeforeEach
    fun graphInit() {
        graph = UndirectedGraph(IntRing)
    }

    @DisplayName("Single vertex graph")
    @Test
    fun singleVertexGraph() {
        graph.addVertex(0)
        assertEquals(1, graph.vertices.size)
        assertTrue(graph.edges.isEmpty())
        assertTrue(algorithm.runOn(graph).isEmpty())
    }

    @DisplayName("Single loop graph")
    @Test
    fun singleLoopGraph() {
        graph.addVertex(0)
        graph.addEdge(0, 0, 0)
        assertEquals(1, graph.vertices.size)
        assertEquals(1, graph.edges.size)
        assertTrue(algorithm.runOn(graph).isEmpty())
    }

    @DisplayName("Single edge graph")
    @Test
    fun singleEdgeGraph() {
        graph.addVertex(0)
        graph.addVertex(1)
        graph.addEdge(0, 1, 0)
        assertEquals(2, graph.vertices.size)
        assertEquals(1, graph.edges.size)
        val result = algorithm.runOn(graph)
        assertEquals(1, result.size)
        assertEquals(2, result.first().size)
        assertTrue(result.first().all { it.element in 0..1 })
    }

    @DisplayName("Multiple edge pseudo-bridge")
    @Test
    fun multipleEdgePseudoBridge() {
        graph.addVertex(0)
        graph.addVertex(1)
        graph.addEdge(0, 1, 0)
        graph.addEdge(0, 1, 1)
        assertEquals(2, graph.vertices.size)
        assertEquals(2, graph.edges.size)
        val result = algorithm.runOn(graph)
        assertTrue(result.isEmpty())
    }

    @DisplayName("Single cycle graph")
    @Test
    fun singleCycleGraph() {
        graph.addVertex(0)
        graph.addVertex(1)
        graph.addVertex(2)
        graph.addEdge(0, 1, 0)
        graph.addEdge(0, 2, 1)
        graph.addEdge(1, 2, 2)
        assertEquals(3, graph.vertices.size)
        assertEquals(3, graph.edges.size)
        val result = algorithm.runOn(graph)
        assertTrue(result.isEmpty())
    }



}