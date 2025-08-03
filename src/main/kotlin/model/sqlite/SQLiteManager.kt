package model.sqlite

import model.APPLICATION_K_TYPE
import model.APPLICATION_V_TYPE
import model.APPLICATION_W_TYPE
import model.Constants
import model.graph.DirectedGraph
import model.graph.Graph
import model.graph.UndirectedGraph
import model.sqlite.SQLiteGraphOperations.castToType
import model.sqlite.SQLiteGraphOperations.determineRingType
import model.sqlite.SQLiteGraphOperations.setupTables
import model.sqlite.SQLiteGraphOperations.writeEdge
import model.sqlite.SQLiteGraphOperations.writeGraphMetadata
import model.sqlite.SQLiteGraphOperations.writeVertex
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * SQLite database manager for a graph
 *
 * Stores graphs in database in format:
 * graphs:
 *      name - unique identifier
 *      V, K, W - Type::class.java.name
 *      directed - Boolean
 * edges:
 *      id - unique identifier - obtains from the name of the graph and the key (key is unique)
 *      graph - graph name
 *      key, start_vertex, end_vertex, weight - corresponding fields in the string representation
 * vertices:
 *      id - unique identifier - obtains from the name of the graph and the value (value is unique)
 *      graph - graph name
 *      value - value in the string representation
 */

object SQLiteManager {

    fun createConnection(): Connection {
        val dbFile = File(Constants.SQLITE_DATABASE_PATH)
        dbFile.parentFile.mkdirs()
        val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        setupTables(conn)

        return conn
    }
    fun <V, K, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, connection: Connection, name: String) {
        requireNotNull(graph.edges.last().key)
        requireNotNull(graph.vertices.last().value)
        writeGraphMetadata(
            connection = connection,
            name = name,
            typeOfV = graph.vertices.last().value!!::class.java.name,
            typeOfK = graph.edges.last().key!!::class.java.name,
            typeOfW = graph.edges.last().weight::class.java.name,
            directedFlag = (graph is DirectedGraph).toString()
        ).execute()

        for (v in graph.vertices) {
            writeVertex(
                connection = connection,
                id = name + "_" + v.value.toString(),
                graphName = name,
                value = v.value.toString()
            ).execute()
        }

        for (e in graph.edges) {
            writeEdge(
                connection = connection,
                graphName = name,
                e = e
            ).execute()
        }
    }
    fun <V: Any, K : Any, W : Comparable<W>>loadGraphFromDatabase(
        connection: Connection,
        name: String
    ): Graph<V, K, W> {
        val graphStmt =
            connection.prepareStatement("SELECT name, V, K, W, directed FROM graphs WHERE name = ?")
        graphStmt.setString(1, name)
        val graphMeta = graphStmt.executeQuery()
        if (!graphMeta.next()) {
            error("Graph named $name not found")
        }
        val typeV = graphMeta.getString("V")
        val typeK = graphMeta.getString("K")
        val typeW = graphMeta.getString("W")
        val ring = determineRingType<W>(typeW)
        val isDirectedGraph = graphMeta.getBoolean("directed")

        val graph = if (isDirectedGraph)
            DirectedGraph<V, K, W>(ring)
        else
            UndirectedGraph(ring)

        val vertexStmt = connection.prepareStatement("SELECT graph, value FROM vertices WHERE graph = ?")
        val edgeStmt =
            connection.prepareStatement("SELECT graph, key, start_vertex, end_vertex, weight FROM edges WHERE graph = ?")
        vertexStmt.setString(1, name)
        edgeStmt.setString(1, name)

        val vertexRows = vertexStmt.executeQuery()
        val edgeRows = edgeStmt.executeQuery()

        while (vertexRows.next()) {
            graph.addVertex(castToType(vertexRows.getString("value"), typeV))
        }
        while (edgeRows.next()) {
            val start: V = castToType(edgeRows.getString("start_vertex"), typeV)
            val end: V = castToType(edgeRows.getString("end_vertex"), typeV)
            val key: K = castToType(edgeRows.getString("key"), typeK)
            val weight: W = castToType(edgeRows.getString("weight"), typeW)
            graph.addEdge(start, end, key, weight)
        }
        return graph
    }

    fun getGraphNames(database: Connection): List<String> {
        val result: MutableList<String> = mutableListOf()
        val namesStmt = database.prepareStatement("SELECT name FROM graphs WHERE V = ? AND K = ? AND W = ?")
        namesStmt.setString(1, (APPLICATION_V_TYPE::class.javaObjectType).name)
        namesStmt.setString(2, (APPLICATION_K_TYPE::class.javaObjectType).name)
        namesStmt.setString(3, (APPLICATION_W_TYPE::class.javaObjectType).name)
        val names = namesStmt.executeQuery()

        while (names.next()) {
            result.add(names.getString("name"))
        }

        return result
    }
}
