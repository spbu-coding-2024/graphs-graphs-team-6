package model.utils

import model.Graph

object SSSPCalculator {
    fun <V, K, W: Comparable<W>> bellmanFordAlgorithm(graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, V>, Map<V, W>> {

        var distance = mutableMapOf<V, W>()
        var predecessor = mutableMapOf<V, V>()
        distance[startVertex] = graph.ring.zero
        graph.vertices.forEach { firstVertex ->
            firstVertex.adjacencyList.forEach { secondVertex ->
                val firstDistance = distance[firstVertex.element]
                val secondDistance = distance[secondVertex.first.element]
                val weight = secondVertex.second
                if (firstDistance != null) {
                    secondDistance?.let {
                        distance[secondVertex.first.element] = graph.ring.add(it, weight)
                    }
                }
            }
        }
        return predecessor.toMap() to distance.toMap()
    }

}