package model.SQLite

import model.graph.Graph
import java.sql.Connection

object GraphService {
    fun <V, K, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, connection: Connection) {
        val vertexStmt = connection.prepareStatement("INSERT OR REPLACE INTO vertices (id, value) VALUES (?, ?)")
        for (v in graph.vertices) {
            vertexStmt.setString(1, v.value.toString()) // assuming V has unique string
            vertexStmt.setString(2, v.adjacencyList.toString())
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
