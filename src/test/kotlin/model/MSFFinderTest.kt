package model

import model.graph.UndirectedGraph
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing

class MSFFinderTest {

    @Test
    fun testEmptyGraph() {
        val graph = UndirectedGraph<String, Int, Int>(IntRing)
        val edgeLists = MSFFinder(graph).findMSFKruskal()
        assertTrue(edgeLists.isEmpty(), "Empty graph should produce no components")
    }

    @Test
    fun testSingleVertex() {
        val graph = UndirectedGraph<String, Int, Int>(IntRing)
        graph.addVertex("A")
        val edgeLists = MSFFinder(graph).findMSFKruskal()

        assertEquals(1, edgeLists.size, "Graph with one vertex should yield one component")
        assertTrue(edgeLists[0].isEmpty(), "Single-vertex MST should have no edges")
    }

    @Test
    fun testSimpleTree() {
        val graph = UndirectedGraph<String, Int, Int>(IntRing)
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")
        graph.addEdge("A", "B", 1, 5)
        graph.addEdge("B", "C", 2, 3)

        val edgeLists = MSFFinder(graph).findMSFKruskal()
        assertEquals(1, edgeLists.size, "Tree graph should yield one component")

        val mst = edgeLists[0]
        assertEquals(2, mst.size, "Tree MST should contain two edges")
        val weights = mst.map { it.weight }
        assertTrue(weights.containsAll(listOf(5, 3)), "MST edges should have weights 3 and 5")
        val totalWeight = weights.sumOf { it }
        assertEquals(8, totalWeight, "MST total weight should be 8")
    }

    @Test
    fun testCycleGraph() {
        val graph = UndirectedGraph<String, Int, Int>(IntRing)
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")
        graph.addEdge("A", "B", 1, 1)
        graph.addEdge("B", "C", 2, 2)
        graph.addEdge("C", "A", 3, 3)

        val edgeLists = MSFFinder(graph).findMSFKruskal()
        assertEquals(1, edgeLists.size, "Cycle graph should yield one component")

        val mst = edgeLists[0]
        assertEquals(2, mst.size, "Cycle MST should contain two edges")
        val weights = mst.map { it.weight }
        assertTrue(weights.containsAll(listOf(1, 2)), "MST should include edges of weight 1 and 2")
        val totalWeight = weights.sumOf { it }
        assertEquals(3, totalWeight, "MST total weight should be 3")
    }

    @Test
    fun testDisconnectedGraph() {
        val graph = UndirectedGraph<String, Int, Int>(IntRing)
        graph.addVertex("A")
        graph.addVertex("B")
        graph.addVertex("C")
        graph.addVertex("D")
        graph.addEdge("A", "B", 1, 4)
        graph.addEdge("C", "D", 2, 7)

        val edgeLists = MSFFinder(graph).findMSFKruskal()
        assertEquals(2, edgeLists.size, "Disconnected graph should yield two components")

        val weights = edgeLists.map { component -> component.sumOf { it.weight } }.sorted()
        assertEquals(listOf(4, 7), weights, "Weights should be 4 and 7")
        assertEquals(1, edgeLists[0].size)
        assertEquals(1, edgeLists[1].size)
    }

    @Test
    fun testMultipleComponentsWithLoopsAndParallelEdges() {
        val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
            listOf("A", "B", "C", "D", "E", "F", "G", "H").forEach { addVertex(it) }
            var idx = 0
            addEdge("A", "B", idx++, 1)
            addEdge("B", "C", idx++, 1)
            addEdge("C", "A", idx++, 1)
            addEdge("C", "C", idx++, 1)

            addEdge("D", "E", idx++, 1)
            addEdge("E", "F", idx++, 1)
            addEdge("F", "D", idx++, 1)

            addEdge("G", "H", idx++, 1)
            addEdge("H", "G", idx++, 1)
        }

        val edgeLists = MSFFinder(graph).findMSFKruskal()
        val totalEdges = edgeLists.sumOf { it.size }
        assertEquals(5, totalEdges, "Should have 5 MST edges across all components")
        assertEquals(3, edgeLists.size, "Should detect three separate components")
        val componentSizes = edgeLists.map { it.size }.sorted()
        assertEquals(listOf(1, 2, 2), componentSizes, "Component MST sizes should be 2,2 and 1")
    }
}
