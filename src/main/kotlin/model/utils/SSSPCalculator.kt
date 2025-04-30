package model.utils

import model.DirectedGraph
import model.Graph
import model.UndirectedGraph

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>>{
        if (graph is DirectedGraph) {
            return bellmanFordAlgorithmForDirected(graph, startVertex)
        } else if (graph is UndirectedGraph) {
            return bellmanFordAlgorithmForUndirected(graph, startVertex)
        } else {
            error("Different graph type detected")
        }
    }
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForDirected(graph: DirectedGraph<V, K, W>, startVertex: V):
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
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForUndirected(graph: UndirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessor = mutableMapOf<V, V>()
        distance[startVertex] = graph.ring.zero
        repeat(graph.vertices.size) {
            graph.edges.forEach { edge ->
                require(edge.pair.size <= 2)
                if (edge.pair.size == 1) null
                val vertices = edge.pair.map { it.element }.toList()
                for (i in 0..1) {
                    distance[vertices[i]]?.let { distFirst ->
                        val newDistance = graph.ring.add(distFirst, edge.weight)

                        distance[vertices[1-i]]?.let { distSecond ->
                            distance[vertices[1-i]] = if (distSecond > newDistance)
                                newDistance else distSecond
                        } ?:let {
                            distance[vertices[1-i]] = newDistance
                        }
                    }
                }
            }
        }
        return predecessor.toMap() to distance.toMap()
    }

}
