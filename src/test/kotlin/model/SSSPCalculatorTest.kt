package model

import model.graph.DirectedGraph
import model.graph.UndirectedGraph
import model.algos.BellmanFordPathCalculator
import model.algos.GraphPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing
import kotlin.random.Random

class SSSPCalculatorTest {

    @Test
    fun `BellmanFord on linkedlist-like directed graph`() {
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
        val (_, map) = BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "A")
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
        val (pred, weight) = BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "A")
        val path = GraphPath.construct(pred, "E")
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
        val (_, map) = BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "s")
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
        val (_, map) = BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "s")
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
        val (pred, weight) = BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "s")
        val path = GraphPath.construct(pred, "z")
        assertEquals(1, path[0].key)
        assertEquals(7, path[1].key)
        assertEquals(4, path[2].key)
        assertEquals(8, path[3].key)
    }

    @Test
    fun `Bellman on linkedlist-like undirected graph`() {
        val testGraph = UndirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")

            var index = 0
            addEdge("A", "B", index++, 1)
            addEdge("B", "C", index++, 1)
            addEdge("C", "D", index++, 1)
            addEdge("D", "E", index++, 1)

        }
        val (_, map) = BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "A")
        assertEquals(0, map["A"])
        assertEquals(1, map["B"])
        assertEquals(2, map["C"])
        assertEquals(3, map["D"])
        assertEquals(4, map["E"])
    }

    @Test
    fun `Bellman detects negative cycle in directed graph`() {
        val testGraph = UndirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("s")
            addVertex("t")
            addVertex("y")
            addVertex("x")
            addVertex("z")
            var index = 0
            addEdge("s", "t", index++, 6)
            addEdge("s", "y", index++, 7)
            addEdge("t", "y", index++, 8)

            addEdge("t", "x", index++, 1)
            addEdge("x", "t", index++, -2)

            addEdge("y", "z", index++, 9)
            addEdge("z", "x", index++, 7)

            addEdge("y", "x", index++, -3)
            addEdge("t", "z", index++, -4)

            addEdge("z", "s", index++, 2)

        }
        try {
            BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(testGraph, "A")
        } catch (e: IllegalStateException) {
            assertEquals(e.message, "There's exist a negative cycle in a graph")
        }
    }

    @RepeatedTest(25)
    fun `Undirected graph with one negative weighted edge will not run on Bellman`(){
        val maxVertices = 100
        val randomGraph = GraphGenerator.generateUndirectedGraph(maxVertices)
        val numOfVertices = randomGraph.vertices.size

        val firstVert = "v${Random.nextInt(1, numOfVertices)}"
        val secondVert = "v${Random.nextInt(1, numOfVertices)}"
        randomGraph.addEdge(firstVert, secondVert, maxVertices, -1)

        try {
            BellmanFordPathCalculator.bellmanFordAlgorithm<String, Int, Int>(randomGraph, "v0")
        } catch (e: IllegalStateException) {
            assertEquals(e.message, "There's exist a negative cycle in a graph")
        }
    }

    @Test
    fun `Bellman-Ford with complicated undirected graph`() {
        val testGraph = UndirectedGraph<String, Int, Int>(IntRing).apply {
            addVertex("A")
            addVertex("B")
            addVertex("C")
            addVertex("D")
            addVertex("E")
            addVertex("F")
            addVertex("G")
            addVertex("H")

            var index = 0
            val weight = Array<Int>(vertices.size * (vertices.size - 1) / 2) {it * 2}

            addEdge("A", "B", index, weight[index]); index++
            addEdge("B", "C", index, weight[index]); index++
            addEdge("C", "A", index, weight[index]); index++
            addEdge("C", "C", index, weight[index]); index++

            addEdge("C", "F", index, weight[index]); index++

            addEdge("D", "E", index, weight[index]); index++
            addEdge("E", "F", index, weight[index]); index++
            addEdge("F", "D", index, weight[index]); index++


            addEdge("H", "D", index, weight[index]); index++

            addEdge("G", "H", index, weight[index]); index++
            addEdge("H", "G", index, 231); index++
        }
        val (pred, map) = BellmanFordPathCalculator.bellmanFordAlgorithm(testGraph, "A")
        assertEquals("F", pred["E"]?.opposite("E"))
        assertEquals("C", pred["F"]?.opposite("F"))
        assertEquals("B", pred["C"]?.opposite("C"))
        assertEquals("A", pred["B"]?.opposite("B"))
    }


}
