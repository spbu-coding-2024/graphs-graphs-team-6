package model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import space.kscience.kmath.operations.IntRing

@DisplayName("Tests for UndirectedGraph")
class UndirectedGraphTest {

    private lateinit var graph: UndirectedGraph<Int, Int, Int>

    @BeforeEach
    fun graphInit() {
        // Передаём графу кольцо целых чисел
        graph = UndirectedGraph(IntRing)
    }

    @DisplayName("One vertex graph")
    @Test
    fun oneVertexGraph() {
        graph.addVertex(0)
        assertEquals(1, graph.vertices.size)
        assertTrue(graph.vertices.any { it.element == 0 })
    }

    @DisplayName("Two vertices and one edge graph")
    @Test
    fun twoVertexAndOneEdgeGraph() {
        graph.addVertex(0)
        graph.addVertex(1)
        // Указываем вес ребра как IntRing.one
        graph.addEdge(0, 1, 10, IntRing.one)

        assertEquals(2, graph.vertices.size)
        assertEquals(1, graph.edges.size)

        assertTrue(graph.vertices.any { it.element == 0 })
        assertTrue(graph.vertices.any { it.element == 1 })

        val edge = graph.edges.first()
        assertEquals(10, edge.key)
        // Проверяем концы
        val elements = edge.pair.map { it.element }.toSet()
        assertEquals(setOf(0, 1), elements)
        // И вес
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
        assertTrue(graph.vertices.any { it.element == 0 })

        val edge = graph.edges.first()
        assertEquals(10, edge.key)
        val elements = edge.pair.map { it.element }.toSet()
        // Для петли будет только один элемент
        assertEquals(setOf(0), elements)
        assertEquals(IntRing.one, edge.weight)
    }
}
