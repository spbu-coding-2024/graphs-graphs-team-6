package model


/**
 * Class for bridge finding algorithm implementation
 */
class BridgeFinder {
    /**
     * Bridge finding algorithm
     *
     * @param graph receives graph where needed to find bridges
     *
     * @return set of pairs, which represents bridge edges
     */
    fun <V, K, W: Comparable<W>> runOn(graph: UndirectedGraph<V, K, W>): Set<Set<Vertex<V>>> {
        val bridges: MutableSet<Set<Vertex<V>>> = mutableSetOf()
        val usedVertices: MutableSet<Vertex<V>> = mutableSetOf()
        val timeIn: MutableMap<Vertex<V>, Int> = mutableMapOf()
        val lowLink: MutableMap<Vertex<V>, Int> = mutableMapOf()
        var timer = 0

        fun dfs(current: Vertex<V>, previous: Vertex<V>? = null) {
            var firstReturn = true
            usedVertices.add(current)
            timeIn[current] = timer
            lowLink[current] = timer
            timer++
            for (next in current.adjacencyList) {
                if (next == previous && firstReturn || next == current) {
                    firstReturn = false
                    continue
                }
                if (next in usedVertices) {
                    lowLink[current] = safeMin(lowLink[current], timeIn[next])
                }
                else {

                    dfs(next, current)
                    lowLink[current] = safeMin(lowLink[current], lowLink[next])
                    if (
                        (lowLink[next] ?: error("Cannot find next vertex(next is not used)"))
                        >
                        (timeIn[current] ?: error("Cannot find current vertex(next is not used)"))
                    ) {
                        bridges.add(setOf(current, next))
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

    private fun safeMin(first: Int?, second: Int?): Int {
        return minOf(
            first ?: error("Cannot find current vertex(next is used)"),
            second ?: error("Cannot find next vertex(next is used)")
        )
    }
}
