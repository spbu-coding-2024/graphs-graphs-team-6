package tests

import model.UndirectedGraph
import model.UndirectedGraph.UndirectedVertex
import model.UndirectedGraph.UndirectedEdge


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
typealias Graph = (UndirectedGraph<Int, Int>)
typealias Vertex = (UndirectedVertex<Int>)
typealias Edge = (UndirectedEdge<Int, Int>)

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
        assert(graph.edges.contains(Edge(Vertex(0), Vertex(1), 10)))
    }

    @DisplayName("Two vertex and one undirected edge graph")
    @Test
    fun twoVertexAndOneUndirectedEdgeGraph() {
        graph.addVertex(0)
        graph.addVertex(1)
        graph.addEdge(0, 1, 10)
        assert(graph.vertices.isNotEmpty())
        assert(graph.vertices.size == 2)
        assert(graph.edges.size == 1)
        assert(graph.vertices.contains(UndirectedVertex(0)))
        assert(graph.vertices.contains(UndirectedVertex(1)))
        assert(graph.edges.contains(UndirectedEdge(UndirectedVertex(0), UndirectedVertex(1), 10)))
        assert(graph.edges.contains(UndirectedEdge(UndirectedVertex(1), UndirectedVertex(0), 10)))
    }
}
