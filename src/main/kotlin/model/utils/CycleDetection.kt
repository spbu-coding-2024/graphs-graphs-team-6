package model.utils

import model.DirectedGraph
import model.DirectedGraph.DirectedVertex
import model.Edge
import model.Graph
import model.UndirectedGraph

import model.UndirectedGraph.UndirectedVertex
import model.Vertex

class CycleDetection {
    private fun <V, K, W: Comparable<W>>
            constructPath(graph: Graph<V, K, W>, predecessors: MutableMap<V, V>, startVertex: V, endVertex: V
                          ): List<Edge<V, K, W>> {
        var endEdge = endVertex
        var startEdge = predecessors[endVertex]
        val list = ArrayList<Edge<V, K, W>>()
        while (startEdge != null && startEdge != startVertex) {
            list.addLast(graph.getEdge(startEdge, endEdge))
            val temp = startEdge
            startEdge = predecessors[startEdge]
            endEdge = temp
        }
        return list.toList()
    }
    fun <V, K, W: Comparable<W>> findCyclesFromGivenVertex(graph: UndirectedGraph<V, K, W>, start: UndirectedVertex<V>):
            List<List<Edge<V, K, W>>> {
        require(graph.vertices.contains(start))
        val predecessor = mutableMapOf<V, V>()
        val isRelaxed = mutableMapOf<Vertex<V>, Boolean>()
        val queue = ArrayDeque<Vertex<V>>()
        val cycles = ArrayList<List< Edge<V, K, W>>>()
        queue.addLast(start)
        while (queue.isNotEmpty()) {
            val current = queue.removeLast()
            for (adjVertex in current.adjacencyList) {
                if (isRelaxed[adjVertex] == false) {
                    queue.addLast(adjVertex)
                    predecessor[adjVertex.value] = current.value
                } else {
                    val adjPredecessor = predecessor[adjVertex.value]
                    if (adjPredecessor == null) error("Relaxed vertex does not have predecessors")
                    val pathToCurrent = constructPath(graph, predecessor, start.value, current.value)
                    val pathToRelaxed =  constructPath(graph, predecessor, start.value, adjVertex.value)
                    cycles.addLast(pathToRelaxed + pathToCurrent)
                }
            }
            isRelaxed[current] = true
        }
        return cycles.toList()
    }

    fun <V, K, W: Comparable<W>> findCyclesFromGivenVertex(graph: DirectedGraph<V, K, W>, start: DirectedVertex<V>):
            List<List<Edge<V, K, W>>> {

        val visited = graph.vertices.associateWith { false }.toMutableMap()
        val predecessor: MutableMap<V, V> = mutableMapOf()
        val cycles = ArrayList<List< Edge<V, K, W>>>()

        fun dfs(vertex: DirectedVertex<V>, visited: MutableMap<DirectedVertex<V>, Boolean>) {
            if (visited[vertex] == false){
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
}