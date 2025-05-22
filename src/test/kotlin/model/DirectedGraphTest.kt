package model

import model.graph.DirectedGraph
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.DisplayName
import space.kscience.kmath.operations.IntRing

class DirectedGraphTest {
    private lateinit var graph: DirectedGraph<String, String, Int>

    @BeforeEach
    fun setup() {
        graph = DirectedGraph(IntRing)
    }

    @Test
    fun testOneVertex() {
        graph.addVertex("A")
        assertEquals(1, graph.vertices.size)
        val vertex = graph.vertices.first()
        assertEquals("A", vertex.value)
    }

    @Test
    fun testTwoVerticesOneEdge() {
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addEdge("A", "B", "e1", IntRing.one)
        assertEquals(2, graph.vertices.size)
        val vA = graph.vertices.first { it.value == "A" }
        val vB = graph.vertices.first { it.value == "B" }
        assertTrue(vA.adjacencyList.contains(vB))
        assertFalse(vB.adjacencyList.contains(vA))
    }

    @Test
    fun testEdgeNonExistent() {
        assertThrows<NoSuchElementException> {
            graph.addEdge("X", "Y", "e", IntRing.one)
        }
        graph.addVertex("A")
        assertThrows<NoSuchElementException> {
            graph.addEdge("A", "Y", "e", IntRing.one)
        }
    }

    @Test
    fun testLoop() {
        graph.addVertex("A")
        graph.addEdge("A", "A", "loop", IntRing.one)
        assertEquals(1, graph.vertices.size)
        val vA = graph.vertices.first { it.value == "A" }
        assertTrue(vA.adjacencyList.contains(vA))
    }

    @DisplayName("getEdge works correctly on linkedlist-like directed graph")
    @Test
    fun getEdgeTest1() {
        val verts = Array<String>(100) {it.toString()}
        val keys= Array<String>(100) { "K$it" }
        verts.forEach { graph.addVertex(it) }
        for (i in 0..98) {
            graph.addEdge(verts[i], verts[i + 1], keys[98-i])
        }

        for (i in 0..98) {
            assertEquals(graph.getEdge(verts[i], verts[i + 1])?.key, keys[98-i])
        }
    }

}
