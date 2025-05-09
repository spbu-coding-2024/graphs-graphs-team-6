package model.utils

import model.Edge
import model.Graph
import java.util.PriorityQueue
import kotlin.collections.ArrayList

class DijkstraPathCalculator {

    fun <V, K, W : Comparable<W>> runOn(
        graph: Graph<V, K, W>,
        startVertex: V
    ): Pair<Map<V, Edge<V, K, W>>, Map<V, W>> {
        val ring = graph.ring
        val distances = mutableMapOf<V, W>()
        val previousEdges = mutableMapOf<V, Edge<V, K, W>>()
        val visited = mutableSetOf<V>()

        val queue = PriorityQueue(compareBy<Pair<W, V>> { it.first })

        distances[startVertex] = ring.zero
        queue.add(ring.zero to startVertex)

        while (queue.isNotEmpty()) {
            val (currentDistance, currentVertexValue) = queue.poll()

            if (!visited.add(currentVertexValue)) continue

            val vertex = graph.vertices.firstOrNull { it.value == currentVertexValue } ?: continue

            for (neighbor in vertex.adjacencyList) {
                val edge = graph.edges.firstOrNull {
                    (it.startVertex.value == currentVertexValue && it.endVertex.value == neighbor.value) ||
                            (it.endVertex.value == currentVertexValue && it.startVertex.value == neighbor.value)
                } ?: continue

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

        return previousEdges to distances
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
