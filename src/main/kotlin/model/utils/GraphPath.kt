package model.utils

import model.graph.Edge

object GraphPath {
    fun <V, K, W: Comparable<W>> construct(predecessors: Map<V, Edge<V, K, W>>,
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
