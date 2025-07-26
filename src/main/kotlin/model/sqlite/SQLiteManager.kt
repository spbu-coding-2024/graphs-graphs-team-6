package model.sqlite

import model.APPLICATION_K_TYPE
import model.APPLICATION_V_TYPE
import model.APPLICATION_W_TYPE
import model.graph.DirectedGraph
import model.graph.Graph
import model.graph.UndirectedGraph
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ShortRing
import space.kscience.kmath.operations.ByteRing
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

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
        val dbFile = File("data/graphs.sqlite")
        dbFile.parentFile.mkdirs()
        val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")

        conn.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
            CREATE TABLE IF NOT EXISTS graphs (
                name TEXT NOT NULL,
                V TEXT NOT NULL,
                K TEXT NOT NULL,
                W TEXT NOT NULL,
                directed BOOLEAN
            )
            """.trimIndent()
            )
            stmt.executeUpdate(
                """
            CREATE TABLE IF NOT EXISTS vertices (
                id TEXT NOT NULL,
                graph TEXT NOT NULL,
                value TEXT NOT NULL
            )
            """.trimIndent()
            )
            stmt.executeUpdate(
                """
            CREATE TABLE IF NOT EXISTS edges (
                id TEXT NOT NULL,
                graph TEXT NOT NULL,
                key TEXT NOT NULL,
                start_vertex TEXT NOT NULL,
                end_vertex TEXT NOT NULL,
                weight TEXT NOT NULL
            )
            """.trimIndent()
            )
        }

        return conn
    }

    private fun <W: Comparable<W>>determineRingType(
        name: String
    ): Ring<W> = try {
        @Suppress("UNCHECKED_CAST")
        when (name) {
            Int::class.java.name -> IntRing // Ring<Int>
            Long::class.java.name -> LongRing // Ring<Long>
            Short::class.java.name -> ShortRing // Ring<Short>
            Byte::class.java.name -> ByteRing // Ring<Byte>
            Double::class.java.name -> Float64Field // Ring<Float>
            Float::class.java.name -> Float32Field // Ring<Double>
            else -> error("Can't load this type of weight. Type: $name")
        } as Ring<W>
    }
    catch (e: ClassCastException){
        error("The weight type $name in the database does not match the required ring type.")
    }

    private fun <T: Any>castToType(
        value: String,
        name: String
    ): T = try {
        @Suppress("UNCHECKED_CAST")
        when (name) {
            Int::class.java.name -> value.toInt() as T
            Long::class.java.name -> value.toLong() as T
            Short::class.java.name -> value.toShort() as T
            Byte::class.java.name -> value.toByte() as T
            Double::class.java.name -> value.toDouble() as T
            Float::class.java.name -> value.toFloat() as T
            Boolean::class.java.name -> value.toBooleanStrict() as T
            String::class.java.name -> value as T
            else -> error("Can't load this type. Type: $name")
        }
    }
    catch (e: ClassCastException){
        error("The type $name in the database does not match the required graph type.")
    }

    fun <V, K, W : Comparable<W>> saveGraphToDatabase(graph: Graph<V, K, W>, connection: Connection, name: String) {
        val graphStmt =
            connection.prepareStatement("INSERT INTO graphs (name, V, K, W, directed) VALUES (?, ?, ?, ?, ?)")

        graphStmt.setString(1, name)

        require(graph.vertices.last().value != null)
        graphStmt.setString(2, graph.vertices.last().value!!::class.java.name)

        require(graph.vertices.last().value != null)
        graphStmt.setString(3, graph.edges.last().key!!::class.java.name)

        graphStmt.setString(4, graph.edges.last().weight::class.java.name)

        graphStmt.setString(5, (graph is DirectedGraph).toString())

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
    fun <V: Any, K : Any, W : Comparable<W>>loadGraphFromDatabase(
        connection: Connection,
        name: String
    ): Graph<V, K, W> {
        val graphStmt =
            connection.prepareStatement("SELECT name, V, K, W, directed FROM graphs WHERE name = ?")
        graphStmt.setString(1, name)
        val graphMeta = graphStmt.executeQuery()
        val typeV: String
        val typeK: String
        val typeW: String
        val ring: Ring<W>
        val isDirectedGraph: Boolean
        try {
            typeV = graphMeta.getString("V")
            typeK = graphMeta.getString("K")
            typeW = graphMeta.getString("W")
            ring = determineRingType(typeW)
            isDirectedGraph = graphMeta.getBoolean("directed")
        }
        catch (_: SQLException)
        {
            error("Can not find graph named $name")
        }
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
        val result: List<String> = mutableListOf()
        val namesStmt = database.prepareStatement("SELECT name FROM graphs WHERE V = ? AND K = ? AND W = ?")
        namesStmt.setString(1, APPLICATION_V_TYPE::class.java.name)
        namesStmt.setString(2, APPLICATION_K_TYPE::class.java.name)
        namesStmt.setString(3, APPLICATION_W_TYPE::class.java.name)
        val names = namesStmt.executeQuery()
        while (names.next()) {
            result.plusElement(names.getString("name"))
        }
        return result
    }
}
