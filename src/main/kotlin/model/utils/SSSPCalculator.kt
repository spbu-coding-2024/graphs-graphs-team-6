package model.utils

import model.DirectedGraph
import model.Edge
import model.Graph
import model.UndirectedGraph

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm
                (graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, Edge<V, K, W>>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessorEdge = mutableMapOf<V, Edge<V, K, W>>()
        distance[startVertex] = graph.ring.zero
        var any = false
        for (i in 0..graph.vertices.size) {
            any = false
            graph.edges.forEach { edge ->
                val firstVertex = edge.startVertex.value
                val secondVertex = edge.endVertex.value
                val firstDist = distance[edge.startVertex.value]
                val secondDist = distance[secondVertex]
                if (firstDist == null) return@forEach
                var newDistance = graph.ring.add(firstDist, edge.weight)

                if (secondDist == null || secondDist > newDistance) {
                    distance[secondVertex] = newDistance
                    predecessorEdge[secondVertex] = edge
                    any = true
                }

                if (graph is DirectedGraph || secondDist == null) return@forEach

                newDistance = graph.ring.add(firstDist, edge.weight)

                if (firstDist > newDistance) {
                    distance[firstVertex] = newDistance
                    predecessorEdge[firstVertex] = edge
                    any = true
                }
            }
            if (any == false) break
        }
        if (any == true) error("There's exist a negative cycle in a graph")
        return predecessorEdge.toMap() to distance.toMap()
    }

    fun <V, K, W: Comparable<W>> constructPathUsingEdges(predecessors: Map<V, Edge<V, K, W>>,
                                                         endVertex: V): List<Edge<V, K, W>> {
        var current = predecessors[endVertex]
        var acknowledged = endVertex
        val array = ArrayList<Edge<V, K, W>>(predecessors.size)

        while (current != null) {
            array.addLast(current)
            acknowledged = current.opposite(acknowledged)
            current = predecessors[acknowledged]
        }

        return array.reversed()
    }
}
