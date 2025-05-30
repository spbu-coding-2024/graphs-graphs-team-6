package model.sqlite

import model.graph.Graph
import java.sql.Connection

object GraphService {
    fun <V, K, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, connection: Connection) {
        val id = 0
        val orientation = 0
        val weightType = 0
        val vertexValueType = 0
        val edgeKeyType = 0
        val graphStmt = connection.prepareStatement("INSERT OR REPLACE INTO graphs (id, orientation, weightType, vertexValueType, edgeKeyType) VALUES ($id, $orientation, $weightType, $vertexValueType, $edgeKeyType)")
        graphStmt.execute()

        val vertexStmt = connection.prepareStatement("INSERT OR REPLACE INTO vertices (value, graph) VALUES (?, ?)")
        for (v in graph.vertices) {
            vertexStmt.setString(1, v.value.toString()) // assuming V has unique string
            vertexStmt.setString(2, id.toString())
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

    fun <V, K, W : Comparable<W>> loadGraphFromDatabase(graph: Graph<V, K, W>, connection: Connection) {
    }
}
