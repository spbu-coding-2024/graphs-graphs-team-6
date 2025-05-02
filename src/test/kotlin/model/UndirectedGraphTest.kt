package model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*
import space.kscience.kmath.operations.IntRing

@DisplayName("Tests for UndirectedGraph")
class UndirectedGraphTest {

    private lateinit var graph: UndirectedGraph<Int, Int, Int>

    @BeforeEach
    fun graphInit() {
        graph = UndirectedGraph(IntRing)
    }

    @DisplayName("One vertex graph")
    @Test
    fun oneVertexGraph() {
        graph.addVertex(0)
        assertEquals(1, graph.vertices.size)
        assertTrue(graph.vertices.any { it.value == 0 })
    }

    @DisplayName("Two vertices and one edge graph")
    @Test
    fun twoVertexAndOneEdgeGraph() {
        graph.addVertex(0)
        graph.addVertex(1)
        graph.addEdge(0, 1, 10, IntRing.one)

        assertEquals(2, graph.vertices.size)
        assertEquals(1, graph.edges.size)

        val edge = graph.edges.first()
        assertEquals(10, edge.key)

        val endpoints = setOf(edge.startVertex.value, edge.endVertex.value)
        assertEquals(setOf(0, 1), endpoints)

        assertEquals(IntRing.one, edge.weight)
    }

    @DisplayName("Add edge with non-existing vertices should throw")
    @Test
    fun incorrectEdge() {
        graph.addVertex(0)
        assertThrows<NoSuchElementException> {
            graph.addEdge(0, 2, 5, IntRing.one)
        }
        assertThrows<NoSuchElementException> {
            graph.addEdge(3, 0, 6, IntRing.one)
        }
    }

    @DisplayName("Graph with loop")
    @Test
    fun graphWithLoop() {
        graph.addVertex(0)
        graph.addEdge(0, 0, 10, IntRing.one)

        assertEquals(1, graph.vertices.size)
        assertEquals(1, graph.edges.size)

        val edge = graph.edges.first()
        assertEquals(10, edge.key)

        // For self-loop both start and end are the same
        assertEquals(0, edge.startVertex.value)
        assertEquals(0, edge.endVertex.value)

        assertEquals(IntRing.one, edge.weight)
    }
}
