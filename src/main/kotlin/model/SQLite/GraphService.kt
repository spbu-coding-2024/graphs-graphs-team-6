package model.SQLite

import model.graph.DirectedGraph
import model.graph.Graph
import java.sql.Connection

object GraphService {
    fun <V, K, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, connection: Connection, name: String) {
        val graphStmt = connection.prepareStatement("INSERT INTO graphs (name, V, K, W, directed) VALUES ($name, ?, ?, ?, ?)")

        require(graph.vertices.last().value != null)
        graphStmt.setString(1, graph.vertices.last().value!!::class.java.name)

        require(graph.vertices.last().value != null)
        graphStmt.setString(2, graph.edges.last().key!!::class.java.name)

        graphStmt.setString(3, graph.edges.last().weight::class.java.name)

        graphStmt.setString(4, (graph is DirectedGraph).toString())

        val vertexStmt = connection.prepareStatement("INSERT OR REPLACE INTO vertices (id, graph, value, adj_list) VALUES (?, ?, ?, ?)")
        for (v in graph.vertices) {
            vertexStmt.setString(1, name + "_" + v.value.toString())
            vertexStmt.setString(2, name)
            vertexStmt.setString(3, v.value.toString())
            vertexStmt.setString(4, v.adjacencyList.toString())
            vertexStmt.addBatch()
        }
        vertexStmt.executeBatch()

        val edgeStmt = connection.prepareStatement("INSERT OR REPLACE INTO edges (key, start_vertex, end_vertex, weight) VALUES (?, ?, ?, ?)")
        for (e in graph.edges) {
            edgeStmt.setString(1, e.key.toString())
            edgeStmt.setString(2, e.startVertex.value.toString())
            edgeStmt.setString(3, e.endVertex.value.toString())
            edgeStmt.setString(4, e.weight.toString())
            edgeStmt.addBatch()
        }
        edgeStmt.executeBatch()
    }
}
