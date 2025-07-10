package model.sqlite

import model.graph.DirectedGraph
import model.graph.Graph
import model.graph.UndirectedGraph
import space.kscience.kmath.operations.Ring
import java.sql.Connection
import kotlin.reflect.KClass

object GraphService {
    private fun parseStringToType(value: String, type: KClass<*>): Any? = when (type) {
        Int::class -> value.toIntOrNull()
        Long::class -> value.toLongOrNull()
        Double::class -> value.toDoubleOrNull()
        Float::class -> value.toFloatOrNull()
        Boolean::class -> value.lowercase() in listOf("true", "1")
        String::class -> value
        else -> throw IllegalArgumentException("Unsupported type: $type")
    }
    fun <V, K, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, connection: Connection, name: String) {
        val graphStmt =
            connection.prepareStatement("INSERT INTO graphs (name, V, K, W, directed) VALUES ($name, ?, ?, ?, ?)")

        require(graph.vertices.last().value != null)
        graphStmt.setString(1, graph.vertices.last().value!!::class.java.name)

        require(graph.vertices.last().value != null)
        graphStmt.setString(2, graph.edges.last().key!!::class.java.name)

        graphStmt.setString(3, graph.edges.last().weight::class.java.name)

        graphStmt.setString(4, (graph is DirectedGraph).toString())

        val vertexStmt =
            connection.prepareStatement("INSERT OR REPLACE INTO vertices (id, graph, value) VALUES (?, ?, ?)")
        for (v in graph.vertices) {
            vertexStmt.setString(1, name + "_" + v.value.toString())
            vertexStmt.setString(2, name)
            vertexStmt.setString(3, v.value.toString())
            vertexStmt.addBatch()
        }
        vertexStmt.executeBatch()

        val edgeStmt =
            connection.prepareStatement("INSERT OR REPLACE INTO edges (id, graph, key, start_vertex, end_vertex, weight) VALUES (?, ?, ?, ?)")
        for (e in graph.edges) {
            edgeStmt.setString(1, e.key.toString())
            edgeStmt.setString(2, e.startVertex.value.toString())
            edgeStmt.setString(3, e.endVertex.value.toString())
            edgeStmt.setString(4, e.weight.toString())
            edgeStmt.addBatch()
        }
        edgeStmt.executeBatch()
    }

    fun <V: Any, K: Any, W : Comparable<W>> loadGraphFromDatabase(
        connection: Connection,
        name: String,
        ring: Ring<W>,
        isDirectedGraph: Boolean,
        typeOfV: KClass<V>,
        typeOfK: KClass<K>,
        typeOfW: KClass<W>
    ): Graph<V, K, W> {
        val graph = if (isDirectedGraph) DirectedGraph<V, K, W>(ring) else UndirectedGraph(ring)
        val vertexStmt = connection.prepareStatement("SELECT graph, value FROM vertices WHERE graph == ?")
        val edgeStmt =
            connection.prepareStatement("SELECT graph, key, start_vertex, end_vertex, weight FROM edges WHERE graph == ?")
        vertexStmt.setString(1, name)
        edgeStmt.setString(1, name)

        val vertexRows = vertexStmt.executeQuery()
        val edgeRows = edgeStmt.executeQuery()

        while (vertexRows.next()){
            graph.addVertex(parseStringToType(vertexRows.getString("value"), typeOfV) as V)
        }
        while (edgeRows.next()){
            val start = parseStringToType(edgeRows.getString("start_vertex"), typeOfV) as V
            val end = parseStringToType(edgeRows.getString("end_vertex"), typeOfV) as V
            val key = parseStringToType(edgeRows.getString("key"), typeOfK) as K
            val weight = parseStringToType(edgeRows.getString("weight"), typeOfW) as W
            graph.addEdge(start, end, key, weight)
        }
        return graph
    }
}
