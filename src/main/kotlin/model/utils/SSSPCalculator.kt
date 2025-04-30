package model.utils

import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import org.jetbrains.skia.FontWeight

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>>{
        return if (graph is DirectedGraph) {
            bellmanFordAlgorithmForDirected(graph, startVertex)
        } else if (graph is UndirectedGraph) {
            bellmanFordAlgorithmForUndirected(graph, startVertex)
        } else {
            error("Different graph type detected")
        }
    }
    private fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForDirected(graph: DirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessor = mutableMapOf<V, V>()
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
                            predecessor[secondVertex] = firstVertex
                            any = true
                        }
                    } ?:let {
                        distance[secondVertex] = newDistance
                        predecessor[secondVertex] = firstVertex
                        any = true
                    }
                }
            }
            if (any == false) break
        }
        return predecessor.toMap() to distance.toMap()
    }
    private fun <V, K, W: Comparable<W>> bellmanFordAlgorithmForUndirected(graph: UndirectedGraph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessor = mutableMapOf<V, V>()
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
                                predecessor[vertices[1-i]] = vertices[i]
                                any = true
                            }
                        } ?:let {
                            distance[vertices[1-i]] = newDistance
                            predecessor[vertices[1-i]] = vertices[i]
                            any = true
                        }


                    }
                }
            }
            if (any == false) break
        }
        return predecessor.toMap() to distance.toMap()
    }

    fun <V, W> constructPath(weights: Map<V, W>, predecessors: Map<V, V>, endVertex: V): List<V> {
        require(weights[endVertex] != null)
        var current = predecessors[endVertex]

        val array = ArrayList<V>(predecessors.size)
        array.addLast(endVertex)
        while (current != null) {
            array.addLast(current)
            current = predecessors[current]
        }
        return array.reversed()
    }
}
