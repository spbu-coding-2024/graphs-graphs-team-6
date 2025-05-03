package model.utils

import model.DirectedGraph
import model.DirectedGraph.DirectedEdge
import model.Edge
import model.Graph
import model.UndirectedGraph
import model.UndirectedGraph.UndirectedEdge
import model.Vertex
import org.jetbrains.skia.FontWeight

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, Edge<V, K, W>>, Map<V, W>>{
        return if (graph is DirectedGraph) {
            bellmanFordAlgorithmForDirected(graph, startVertex)
        } else if (graph is UndirectedGraph) {
            TODO()
        } else {
            error("Different graph type detected")
        }
    }
    private fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForDirected
                (graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, Edge<V, K, W>>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessorEdge = mutableMapOf<V, Edge<V, K, W>>()
        distance[startVertex] = graph.ring.zero
        for (i in 0..(graph.vertices.size - 1)) {
            var any = false
            graph.edges.forEach { edge ->
                val secondVertex = edge.endVertex.value
                val firstDist = distance[edge.startVertex.value]
                val secondDist = distance[secondVertex]
                if (firstDist == null) return@forEach
                val newDistance = graph.ring.add(firstDist, edge.weight)

                if (secondDist == null || secondDist > newDistance) {
                    distance[secondVertex] = newDistance
                    predecessorEdge[secondVertex] = edge
                    any = true
                }
            }
            if (any == false) break
        }
        return predecessorEdge.toMap() to distance.toMap()
    }

    fun <V, K, W: Comparable<W>> constructPath(predecessors: Map<V, Edge<V, K, W>>,
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
