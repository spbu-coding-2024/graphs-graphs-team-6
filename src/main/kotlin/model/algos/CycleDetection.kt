package model.algos

import model.graph.DirectedGraph
import model.graph.DirectedGraph.DirectedVertex
import model.graph.Edge
import model.graph.Graph
import model.graph.UndirectedGraph
import model.graph.UndirectedGraph.UndirectedVertex
import model.graph.Vertex

class CycleDetection {
    private fun <V, K, W: Comparable<W>>
            constructPath(graph: Graph<V, K, W>, predecessors: MutableMap<V, V>, startVertex: V, endVertex: V
                          ): List<Edge<V, K, W>> {
        var endEdge = endVertex
        var startEdge = predecessors[endVertex]
        val list = ArrayList<Edge<V, K, W>>()
        do {
            if (startEdge == null) break
            val edge = graph.getEdge(startEdge, endEdge) ?: error("No such edge")
            list.addLast(edge)
            val temp = startEdge
            startEdge = predecessors[startEdge]
            endEdge = temp
        } while (endEdge != startVertex)
        return list.toList()


    }

    fun <V, K, W: Comparable<W>> findCyclesFromGivenVertex(graph: Graph<V, K, W>, start: Vertex<V>):
            List<List<Edge<V, K, W>>> {
        return when(graph) {
            is DirectedGraph -> findCyclesFromGivenVertex(graph, start as DirectedVertex)
            is UndirectedGraph -> findCyclesFromGivenVertex(graph, start as UndirectedVertex)
            else -> error("No support for such graph")
        }
    }

    fun <V, K, W: Comparable<W>> findCyclesFromGivenVertex(graph: DirectedGraph<V, K, W>, start: DirectedVertex<V>):
            List<List<Edge<V, K, W>>> {

        val visited = graph.vertices.associateWith { false }.toMutableMap()
        val predecessor: MutableMap<V, V> = mutableMapOf()
        val cycles = ArrayList<List< Edge<V, K, W>>>()

        fun dfs(vertex: DirectedVertex<V>, visited: MutableMap<DirectedVertex<V>, Boolean>) {
            if (visited[vertex] == true){
                if (vertex == start) {
                    cycles.addLast(constructPath(graph, predecessor, start.value, start.value))
                }
                return
            }
            visited[vertex] = true
            for (adj in vertex.adjacencyList) {
                predecessor[adj.value] = vertex.value
                dfs(adj, visited)
            }
            visited[vertex] = false
        }
        dfs(start, visited)
        return cycles.toList()
    }
    fun <V, K, W: Comparable<W>> findCyclesFromGivenVertex(graph: UndirectedGraph<V, K, W>, start: UndirectedVertex<V>):
            List<List<Edge<V, K, W>>> {

        val visited = graph.vertices.associateWith { false }.toMutableMap()
        val predecessor: MutableMap<V, V> = mutableMapOf()
        val cycles = ArrayList<List< Edge<V, K, W>>>()
        var lastVisitedVertex = start

        fun dfs(vertex: UndirectedVertex<V>, visited: MutableMap<UndirectedVertex<V>, Boolean>) {
            if (visited[vertex] == true){
                if (vertex == start) {
                    cycles.addLast(constructPath(graph, predecessor, start.value, start.value))
                }
                return
            }
            visited[vertex] = true

            for (adj in vertex.adjacencyList) {
                if (lastVisitedVertex == adj) continue
                lastVisitedVertex = vertex
                predecessor[adj.value] = vertex.value

                dfs(adj, visited)
            }
            visited[vertex] = false
        }
        dfs(start, visited)
        return cycles.toList()
    }
}
