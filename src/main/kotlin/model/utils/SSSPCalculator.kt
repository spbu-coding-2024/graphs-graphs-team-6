package model.utils

import model.DirectedGraph
import model.Graph
import model.Vertex
import kotlin.math.min

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: DirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessor = mutableMapOf<V, V>()
        distance[startVertex] = graph.ring.zero
        for (j in 0..(graph.vertices.size - 1)) {
            graph.edges.forEach { edge ->
                require(edge.pair.size <= 2)
                distance[edge.firstVertex.element]?.let { distFirst ->
                    val newDistance = graph.ring.add(distFirst, edge.weight)
                    distance[edge.secondVertex.element]?.let { distSecond ->
                        distance[edge.secondVertex.element] = if (distSecond > newDistance)
                            newDistance else distSecond
                    } ?:let {
                        distance[edge.secondVertex.element] = newDistance
                    }
                }
                null
            }
        }
        return predecessor.toMap() to distance.toMap()
    }

}