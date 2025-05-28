package model

import model.graph.UndirectedGraph
import space.kscience.kmath.operations.IntRing
import kotlin.random.Random

object RandomUndirectedIntGraph {
    /**
     * Get random graph for tests
     * @return random graph
     */
    fun get(maxVertices: Int = 1000, maxWeight: Int = 100): UndirectedGraph<Int, Int, Int> {
        val numOfVertices = Random.nextInt(1, maxVertices)
        val numOfEdges = Random.nextInt(1, numOfVertices * (numOfVertices - 1) / 2 + 1)
        val randomGraph = UndirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..(numOfVertices - 1)) addVertex(i)

            for (i in 0..(numOfEdges - 1)) {
                val firstVert = Random.nextInt(0, numOfVertices - 1)
                val secondVert = Random.nextInt(0, numOfVertices - 1)
                val randomWeight = Random.nextInt(0, maxWeight)
                addEdge(firstVert, secondVert, i, randomWeight)
            }
        }
        return randomGraph
    }


}
