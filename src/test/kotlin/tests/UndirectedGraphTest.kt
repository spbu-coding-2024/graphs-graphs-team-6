package tests

import model.UndirectedGraph
import model.Vertex
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UndirectedGraphTest {

    private var graph: UndirectedGraph<Int, Int> = UndirectedGraph()
    @BeforeEach
    fun graphInit() {
        graph = UndirectedGraph()
    }

    @DisplayName("One vertex graph")
    @Test
    fun oneVertexGraph() {
        graph.addVertex(0)
        assert(graph.vertices.isNotEmpty())
        assert(graph.vertices.size == 1)
        assert(graph.vertices.contains(UndirectedGraph.UndirectedVertex(0)))
    }
}
