package model.utils

import model.DirectedGraph
import model.Graph

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>>{
        if (graph is DirectedGraph) { //temporary
            return bellmanFordAlgorithm(graph, startVertex)
        }
        TODO("Undirected graph")
    }
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: DirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessor = mutableMapOf<V, V>()
        distance[startVertex] = graph.ring.zero
        repeat(graph.vertices.size) {
            graph.edges.forEach { edge ->
                val firstVertex = edge.firstVertex.element
                val secondVertex = edge.secondVertex.element
                require(edge.pair.size <= 2)

                distance[firstVertex]?.let { distFirst ->
                    val newDistance = graph.ring.add(distFirst, edge.weight)

                    distance[secondVertex]?.let { distSecond ->
                        distance[secondVertex] = if (distSecond > newDistance)
                            newDistance else distSecond
                    } ?:let {
                        distance[secondVertex] = newDistance
                    }

                }
            }
        }
        return predecessor.toMap() to distance.toMap()
    }

}