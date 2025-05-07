package model.utils

import model.Edge
import model.Graph

class DijkstraPathCalculator {
    fun <V, K, W: Comparable<W>> runOn(graph: Graph<V, K, W>, startVertex: V):
            Pair<Map<V, Edge<V, K, W>>, Map<V, W>> {
        return Pair(emptyMap(), emptyMap())
    }
}