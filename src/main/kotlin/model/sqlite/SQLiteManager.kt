package model.sqlite

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
import kotlin.reflect.KClass
import kotlin.reflect.cast

object SQLiteManager {
    fun createConnection(): Connection {
        val dbFile = File("data/graphs.sqlite")
        dbFile.parentFile.mkdirs()
        val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")

        conn.createStatement().use { stmt ->
            stmt.executeUpdate(
                """
            CREATE TABLE IF NOT EXISTS graphs (
                name TEXT NOT NULL
                V TEXT NOT NULL,
                K TEXT NOT NULL,
                W TEXT NOT NULL,
                directed BOOLEAN
            )
            """.trimIndent()
            )
        }

        return conn
    }

    private fun determineRingType(
        name: String
    ): Ring<*> = when (name) {
        Int::class.java.name -> IntRing
        Long::class.java.name -> LongRing
        Short::class.java.name -> ShortRing
        Byte::class.java.name -> ByteRing
        Double::class.java.name -> Float64Field
        Float::class.java.name -> Float32Field
        else -> error("Can't load this type of weight. Type: $name")
    }
    private fun determineType(
        name: String
    ): KClass<*> = when (name) {
        Int::class.java.name -> Int::class
        Long::class.java.name -> Long::class
        Short::class.java.name -> Short::class
        Byte::class.java.name -> Byte::class
        Double::class.java.name -> Double::class
        Float::class.java.name -> Float::class
        Boolean::class.java.name -> Boolean::class
        String::class.java.name -> String::class
        else -> error("Can't load this type. Type: $name")
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

    fun loadGraphFromDatabase(
        connection: Connection,
        name: String
    ): Pair<Graph<*, *, *>, Triple<KClass<*>, KClass<*>, KClass<*>>> {
        val graphStmt =
            connection.prepareStatement("SELECT name, V, K, W, directed FROM graphs WHERE name = ?")
        graphStmt.setString(1, name)
        val graphMeta = graphStmt.executeQuery()
        val V: KClass<*>
        val K: KClass<*>
        val W: KClass<*>
        val ring: Ring<*>
        val isDirectedGraph: Boolean
        try {
            V = determineType(graphMeta.getString("V"))
            K = determineType(graphMeta.getString("K"))
            W = determineType(graphMeta.getString("W"))
            ring = determineRingType(graphMeta.getString("W"))
            isDirectedGraph = graphMeta.getBoolean("directed")
        }
        catch (_: SQLException)
        {
            error("Can not find graph named $name")
        }
        val graph = if (isDirectedGraph)
            DirectedGraph<Any, Any, Comparable<Any>>(ring)
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
            graph.addVertex(V.cast(vertexRows.getString("value")))
        }
        while (edgeRows.next()) {
            val start = V.cast(edgeRows.getString("start_vertex"))
            val end = V.cast(edgeRows.getString("end_vertex"))
            val key = K.cast(edgeRows.getString("key"))
            val weight = W.cast(edgeRows.getString("weight"))
            require(weight::class == W) { "Inconsistent weight in database, weight $weight must be $W" }
            graph.addEdge(start, end, key, weight as Comparable<Any>)
        }
        return graph to Triple(V, K, W)
    }

    fun getGraphNames(database: Connection): List<String> {
        val result: List<String> = mutableListOf()
        val namesStmt = database.prepareStatement("SELECT name FROM graphs")
        val names = namesStmt.executeQuery()
        while (names.next()) {
            result.plusElement(names.getString("name"))
        }
        return result
    }
}
