package model.sqlite

import model.graph.DirectedGraph
import model.graph.Graph
import model.graph.UndirectedGraph
import java.sql.Connection
import java.sql.SQLException

/**
 * SQLite database manager for a graph
 *
 * Stores graphs in database in format:
 *
 *
 * Table "graphs":
 *
 *      name - unique identifier
 *
 *      V, K, W - Type::class.java.name
 *
 *      directed - Boolean
 *
 *
 * Table "edges":
 *
 *      id - unique identifier - obtains from the name of the graph and the key (key is unique)
 *
 *      graph - graph name
 *
 *      key, start_vertex, end_vertex, weight - corresponding fields in the string representation
 *
 *
 * Table "vertices":
 *
 *      id - unique identifier - obtains from the name of the graph and the value (value is unique)
 *
 *      graph - graph name
 *
 *      value - value in the string representation
 */

class SQLiteManager(val connection: Connection = createConnection()) {
    fun <V : Any, K : Any, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, name: String) {
        val writeManager = SQLiteWriteConnectionManager(connection)

        connection.autoCommit = false
        try {
            cleanupGraph(connection, name)
            writeManager.setMetadataValues(
                // BE CAREFUL: N/D is not processed.
                // Correctness relies on the absence of type casts in case of empty graph
                metadata = GraphMetadata(
                    name = name,
                    typeV = if (graph.vertices.isNotEmpty()) graph.vertices.last().value::class.java.name else "N/D",
                    typeK = if (graph.edges.isNotEmpty()) graph.edges.last().key::class.java.name else "N/D",
                    typeW = graph.ring.zero::class.java.name,
                    directed = (graph is DirectedGraph)
                )
            )
            writeManager.metadataStmt.execute()

            for (v in graph.vertices) {
                writeManager.setVertexValues(
                    id = name + "_" + v.value.toString(),
                    graphName = name,
                    value = v.value.toString()
                )
            }
            writeManager.vertexStmt.executeBatch()

            for (e in graph.edges) {
                writeManager.setEdgeValues(
                    id = name + "_" + e.key.toString(),
                    graph = name,
                    edge = EdgeRow(
                        key = e.key.toString(),
                        start = e.startVertex.value.toString(),
                        end = e.endVertex.value.toString(),
                        weight = e.weight.toString()
                    )
                )
            }
            writeManager.edgeStmt.executeBatch()
            connection.commit()
        } catch (ex: SQLException) {
            connection.rollback()
            throw ex
        } finally {
            connection.autoCommit = true
        }
    }

    fun <V : Any, K : Any, W : Comparable<W>> loadGraphFromDatabase(name: String): Graph<V, K, W> {
        val metadata = loadGraphMetadata(connection, name)
        val ring = determineRingType<W>(metadata.typeW)
        val graph = if (metadata.directed) DirectedGraph<V, K, W>(ring)
        else UndirectedGraph(ring)


        val vertexValues = loadVertexValues(connection, name)
        val edgeValues = loadEdgeValues(connection, name)

        vertexValues.forEach {
            val v: V = castToType(it, metadata.typeV)
            graph.addVertex(v)
        }

        edgeValues.forEach { edge ->
            val start: V = castToType(edge.start, metadata.typeV)
            val end: V = castToType(edge.end, metadata.typeV)
            val key: K = castToType(edge.key, metadata.typeK)
            val weight: W = castToType(edge.weight, metadata.typeW)
            graph.addEdge(start, end, key, weight)
        }

        return graph
    }
}
