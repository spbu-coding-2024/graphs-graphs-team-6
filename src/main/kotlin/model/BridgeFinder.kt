package model


class BridgeFinder {
    fun <V, K, W: Comparable<W>> runOn(graph: UndirectedGraph<V, K, W>): Set<Pair<Vertex<V>, Vertex<V>>> {
        val bridges: MutableSet<Pair<Vertex<V>, Vertex<V>>> = mutableSetOf()
        val usedVertices: MutableSet<Vertex<V>> = mutableSetOf()
        val timeIn: MutableMap<Vertex<V>, Int> = mutableMapOf()
        val lowLink: MutableMap<Vertex<V>, Int> = mutableMapOf()
        var timer: Int = 0

        fun dfs(current: Vertex<V>, previous: Vertex<V>? = null) {
            usedVertices.add(current)
            timeIn[current] = timer
            lowLink[current] = timer
            timer++
            for (next in current.adjacencyList) {
                if (next == previous || next == current) continue
                if (next in usedVertices) {
                    lowLink[current] =
                        minOf(
                            lowLink[current] ?: error("Cannot find current vertex(next is used)"),
                            timeIn[next] ?: error("Cannot find next vertex(next is used)")
                        )
                }
                else {
                    dfs(next, current)
                    lowLink[current] =
                        minOf(
                            lowLink[current] ?: error("Cannot find current vertex(next is not used)"),
                            lowLink[next] ?: error("Cannot find next vertex(next is not used)")
                        )
                    if (
                        (lowLink[next] ?: error("Cannot find next vertex(next is not used)"))
                        >
                        (timeIn[current] ?: error("Cannot find current vertex(next is not used)"))
                    ) {
                        bridges.add(if (current.hashCode() < next.hashCode()) current to next else next to current)
                    }
                }
            }
        }

        for (v in graph.vertices) {
            if (v !in usedVertices) {
                dfs(v)
            }
        }


        return bridges
    }
}
