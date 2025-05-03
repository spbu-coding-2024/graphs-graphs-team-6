package model

import model.utils.SSSPCalculator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing

class SSSPCalculatorTest {

    @Test
    fun `BellmanFord on linked list like graph`() {
        val testGraph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")

            var index = 0
            addEdge("A", "B", index++, -3)
            addEdge("B", "C", index++, 1)
            addEdge("C", "D", index++, 1)
            addEdge("D", "E", index++, 1)

        }
        val (_, map) = SSSPCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "A")
        assertEquals(0, map["A"])
        assertEquals(-3, map["B"])
        assertEquals(-2, map["C"])
        assertEquals(-1, map["D"])
        assertEquals(0, map["E"])

    }
    @Test
    fun `Construct shortest path on linked list like graph using BellmanFord`() {
        val testGraph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")

            var index = 0
            addEdge("A", "B", index++, -3)
            addEdge("B", "C", index++, 1)
            addEdge("C", "D", index++, 1)
            addEdge("D", "E", index++, 1)

        }
        val (pred, weight) = SSSPCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "A")
        val path = SSSPCalculator.constructPathUsingEdges(pred, "E")
        for (i in 0..3) assertEquals(i, path[i].key)
    }
    @Test
    fun `BellmanFord with symmetric edge`() {
        val testGraph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("s")
            addVertex("t")
            var index = 0
            addEdge("s", "t", index++, 1)
            addEdge("t", "s", index++, -1)
        }
        val (_, map) = SSSPCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "s")
        assertEquals(0, map["s"])
        assertEquals(1, map["t"])
    }
    @Test
    fun `BellmanFord finds a better path`() {
        val testGraph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("s")
            addVertex("t")
            addVertex("y")
            addVertex("x")
            addVertex("z")
            var index = 0
            addEdge("s", "t", index++, 6)
            addEdge("s", "y", index++, 7)
            addEdge("t", "y", index++, 8)

            addEdge("t", "x", index++, 5)
            addEdge("x", "t", index++, -2)

            addEdge("y", "z", index++, 9)
            addEdge("z", "x", index++, 7)

            addEdge("y", "x", index++, -3)
            addEdge("t", "z", index++, -4)

            addEdge("z", "s", index++, 2)
        }
        val (_, map) = SSSPCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "s")
        assertEquals(0, map["s"])
        assertEquals(2, map["t"])
        assertEquals(7, map["y"])
        assertEquals(4, map["x"])
        assertEquals(-2, map["z"])
    }

    @Test
    fun `Construct path on more complicated graph using BellmanFord`() {
        val testGraph = DirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("s")
            addVertex("t")
            addVertex("y")
            addVertex("x")
            addVertex("z")
            var index = 0
            addEdge("s", "t", index++, 6)
            addEdge("s", "y", index++, 7)
            addEdge("t", "y", index++, 8)

            addEdge("t", "x", index++, 5)
            addEdge("x", "t", index++, -2)

            addEdge("y", "z", index++, 9)
            addEdge("z", "x", index++, 7)

            addEdge("y", "x", index++, -3)
            addEdge("t", "z", index++, -4)

            addEdge("z", "s", index++, 2)
        }
        val (pred, weight) = SSSPCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "s")
        val path = SSSPCalculator.constructPathUsingEdges(pred, "z")
        assertEquals(1, path[0].key)
        assertEquals(7, path[1].key)
        assertEquals(4, path[2].key)
        assertEquals(8, path[3].key)
    }
}
