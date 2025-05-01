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
            bellmanFordAlgorithmForUndirected(graph, startVertex)
        } else {
            error("Different graph type detected")
        }
    }
    private fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForDirected(graph: DirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, DirectedEdge<V, K, W>>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessorEdge = mutableMapOf<V, DirectedEdge<V, K, W>>()
        distance[startVertex] = graph.ring.zero
        for (j in 0..(graph.vertices.size - 1)) {
            var any = false
            graph.edges.forEach { edge ->
                val firstVertex = edge.firstVertex.element
                val secondVertex = edge.secondVertex.element
                require(edge.pair.size <= 2)

                distance[firstVertex]?.let { distFirst ->
                    val newDistance = graph.ring.add(distFirst, edge.weight)

                    distance[secondVertex]?.let { distSecond ->
                        if (distSecond > newDistance) {
                            distance[secondVertex] = newDistance
                            predecessorEdge[secondVertex] = edge
                            any = true
                        }
                    } ?:let {
                        distance[secondVertex] = newDistance
                        predecessorEdge[secondVertex] = edge
                        any = true
                    }
                }
            }
            if (any == false) break
        }
        return predecessorEdge.toMap() to distance.toMap()
    }
    private fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForUndirected(graph: UndirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, UndirectedEdge<V, K, W>>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessorEdge = mutableMapOf<V, UndirectedEdge<V, K, W>>()
        distance[startVertex] = graph.ring.zero
        for (j in 0..(graph.vertices.size - 1)) {
            var any = false
            graph.edges.forEach { edge ->
                require(edge.pair.size <= 2)
                if (edge.pair.size == 1) null
                val vertices = edge.pair.map { it.element }.toList()
                for (i in 0..1) {
                    distance[vertices[i]]?.let { distFirst ->
                        val newDistance = graph.ring.add(distFirst, edge.weight)

                        distance[vertices[1-i]]?.let { distSecond ->
                            if (distSecond > newDistance) {
                                distance[vertices[1-i]] = newDistance
                                predecessorEdge[vertices[1-i]] = edge
                                any = true
                            }
                        } ?:let {
                            distance[vertices[1-i]] = newDistance
                            predecessorEdge[vertices[1-i]] = edge
                            any = true
                        }
                    }
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
            require(current.pair.size == 2)
            array.addLast(current)
            acknowledged = current.opposite(acknowledged)
            current = predecessors[acknowledged]
        }

        return array.reversed()
    }
}
