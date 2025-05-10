package model.utils

import model.Edge
import model.Graph
import model.Vertex
import java.util.PriorityQueue

class DijkstraPathCalculator {

    private fun <V, K, W : Comparable<W>> valueToVertexMap(graph: Graph<V, K, W>): Map<V, Vertex<V>> {
        val result: MutableMap<V, Vertex<V>> = mutableMapOf()
        for (vertex in graph.vertices) {
            result[vertex.value] = vertex
        }
        return result
    }

    private fun <V, K, W : Comparable<W>> valuesToEdgeMap(graph: Graph<V, K, W>): Map<Pair<V, V>, List<Edge<V, K, W>>> {
        val result: MutableMap<Pair<V, V>, MutableList<Edge<V, K, W>>> = mutableMapOf()
        for (edge in graph.edges) {
            if (result[edge.startVertex.value to edge.endVertex.value] == null) {
                result[edge.startVertex.value to edge.endVertex.value] = mutableListOf()
            }
            result[edge.startVertex.value to edge.endVertex.value]?.add(edge)
        }
        return result
    }

    fun <V, K, W : Comparable<W>> runOn(
        graph: Graph<V, K, W>,
        startVertex: V
    ): Pair<Map<V, Edge<V, K, W>>, Map<V, W>> {
        val ring = graph.ring
        val distances = mutableMapOf<V, W>()
        val previousEdges = mutableMapOf<V, Edge<V, K, W>>()
        val visited = mutableSetOf<V>()

        val queue = PriorityQueue(compareBy<Pair<W, V>> { it.first })

        val vertices = valueToVertexMap(graph)
        val edges = valuesToEdgeMap(graph)

        distances[startVertex] = ring.zero
        queue.add(ring.zero to startVertex)

        while (queue.isNotEmpty()) {
            val (currentDistance, currentVertexValue) = queue.poll()

            if (!visited.add(currentVertexValue)) continue

            val vertex = vertices[currentVertexValue] ?: continue

            for (neighbor in vertex.adjacencyList) {
                val multipleEdge =
                    edges[currentVertexValue to neighbor.value]?:
                    edges[neighbor.value to currentVertexValue]?:
                    continue

                for (edge in multipleEdge) {
                    val weight = edge.weight
                    require(weight >= ring.zero) { "Edge weights must be non-negative" }

                    val neighborValue = neighbor.value
                    val newDist = ring.add(currentDistance, weight)

                    val currentNeighborDistance = distances[neighborValue]
                    if (currentNeighborDistance == null || newDist < currentNeighborDistance) {
                        distances[neighborValue] = newDist
                        previousEdges[neighborValue] = edge
                        queue.add(newDist to neighborValue)
                    }
                }
            }
        }

        return previousEdges to distances
    }
}
