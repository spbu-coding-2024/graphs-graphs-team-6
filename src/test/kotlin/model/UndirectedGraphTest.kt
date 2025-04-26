package model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tests.Edge
import tests.Graph
import tests.Vertex

class UndirectedGraphTest {

    private var graph: UndirectedGraph<Int, Int> = UndirectedGraph()
    @BeforeEach
    fun graphInit() {
        graph = Graph()
    }

    @DisplayName("One vertex graph")
    @Test
    fun oneVertexGraph() {
        graph.addVertex(0)
        assert(graph.vertices.isNotEmpty())
        assert(graph.vertices.size == 1)
        assert(graph.vertices.contains(Vertex(0)))
    }

    @DisplayName("Two vertex and one edge graph")
    @Test
    fun twoVertexAndOneEdgeGraph() {
        graph.addVertex(0)
        graph.addVertex(1)
        graph.addEdge(0, 1, 10)
        assert(graph.vertices.isNotEmpty())
        assert(graph.vertices.size == 2)
        assert(graph.edges.size == 1)
        assert(graph.vertices.contains(Vertex(0)))
        assert(graph.vertices.contains(Vertex(1)))
        assert(graph.edges.contains(Edge(setOf(Vertex(0), Vertex(1)), 10)))
    }

    @DisplayName("Incorrect edge")
    @Test
    fun incorrectEdge() {
	    assertThrows<IllegalArgumentException> { Edge(setOf(), 0) }
    }

    @DisplayName("Graph with loop")
    @Test
    fun graphWithLoop() {
        graph.addVertex(0)
        graph.addEdge(0, 0, 10)
        assert(graph.vertices.isNotEmpty())
        assert(graph.vertices.size == 1)
        assert(graph.edges.size == 1)
        assert(graph.vertices.contains(Vertex(0)))
        assert(graph.edges.contains(Edge(setOf(Vertex(0)), 10)))
    }
}