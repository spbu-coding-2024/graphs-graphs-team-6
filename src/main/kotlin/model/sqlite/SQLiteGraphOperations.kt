package model.sqlite

import model.APPLICATION_K_TYPE
import model.APPLICATION_V_TYPE
import model.APPLICATION_W_TYPE
import model.Constants
import space.kscience.kmath.operations.ByteRing
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ShortRing
import java.io.File
import java.sql.Connection
import java.sql.DriverManager


fun createConnection(): Connection {
    val dbFile = File(Constants.SQLITE_DATABASE_PATH)
    dbFile.parentFile.mkdirs()
    val conn = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
    setupTables(conn)

    return conn
}

fun setupTables(conn: Connection) {
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
}

@Suppress("UNCHECKED_CAST")
fun <W : Comparable<W>> determineRingType(
    name: String
): Ring<W> = when (name) {
    Int::class.javaObjectType.name -> IntRing // Ring<Int>
    Long::class.javaObjectType.name -> LongRing // Ring<Long>
    Short::class.javaObjectType.name -> ShortRing // Ring<Short>
    Byte::class.javaObjectType.name -> ByteRing // Ring<Byte>
    Double::class.javaObjectType.name -> Float64Field // Ring<Float>
    Float::class.javaObjectType.name -> Float32Field // Ring<Double>
    else -> error("Can't load this type of weight. Type: $name")
} as Ring<W>

@Suppress("UNCHECKED_CAST")
fun <T : Any> castToType(
    value: String, name: String
): T = when (name) {
    Int::class.javaObjectType.name -> value.toInt() as T
    Long::class.javaObjectType.name -> value.toLong() as T
    Short::class.javaObjectType.name -> value.toShort() as T
    Byte::class.javaObjectType.name -> value.toByte() as T
    Double::class.javaObjectType.name -> value.toDouble() as T
    Float::class.javaObjectType.name -> value.toFloat() as T
    Boolean::class.javaObjectType.name -> value.toBooleanStrict() as T
    String::class.javaObjectType.name -> value as T
    else -> error("Can't load this type. Type: $name")
}


fun getGraphNames(connection: Connection): List<String> {
    val result: MutableList<String> = mutableListOf()
    val namesStmt = connection.prepareStatement("SELECT name FROM graphs WHERE V = ? AND K = ? AND W = ?")
    var substitutionIndex = 1
    namesStmt.setString(substitutionIndex++, (APPLICATION_V_TYPE::class.javaObjectType).name)
    namesStmt.setString(substitutionIndex++, (APPLICATION_K_TYPE::class.javaObjectType).name)
    namesStmt.setString(substitutionIndex, (APPLICATION_W_TYPE::class.javaObjectType).name)
    val names = namesStmt.executeQuery()

    while (names.next()) {
        result.add(names.getString("name"))
    }

    return result
}

fun loadGraphMetadata(
    connection: Connection,
    name: String
): GraphMetadata {
    val stmt = connection.prepareStatement(
        "SELECT name, V, K, W, directed FROM graphs WHERE name = ?"
    )
    stmt.setString(1, name)
    val queryResults = stmt.executeQuery()

    if (!queryResults.next()) {
        error("Graph named $name not found")
    }

    return GraphMetadata(
        name = queryResults.getString("name"),
        typeV = queryResults.getString("V"),
        typeK = queryResults.getString("K"),
        typeW = queryResults.getString("W"),
        directed = queryResults.getBoolean("directed")
    )
}

fun loadVertexValues(
    connection: Connection,
    name: String
): List<String> {
    val stmt = connection.prepareStatement(
        "SELECT value FROM vertices WHERE graph = ?"
    )
    stmt.setString(1, name)
    val queryResults = stmt.executeQuery()

    val result = mutableListOf<String>()
    while (queryResults.next()) {
        result.add(queryResults.getString("value"))
    }
    return result
}

fun loadEdgeValues(
    connection: Connection,
    name: String
): List<EdgeRow> {
    val stmt = connection.prepareStatement(
        "SELECT key, start_vertex, end_vertex, weight FROM edges WHERE graph = ?"
    )
    stmt.setString(1, name)
    val queryResults = stmt.executeQuery()

    val result = mutableListOf<EdgeRow>()
    while (queryResults.next()) {
        result.add(
            EdgeRow(
                key = queryResults.getString("key"),
                start = queryResults.getString("start_vertex"),
                end = queryResults.getString("end_vertex"),
                weight = queryResults.getString("weight")
            )
        )
    }
    return result
}

fun cleanupGraph(conn: Connection, graphName: String) {
    conn.prepareStatement(
        """
            DELETE FROM edges
            WHERE graph = ?
            """.trimIndent()
    ).use { stmt ->
        stmt.setString(1, graphName)
        stmt.executeUpdate()
    }

    conn.prepareStatement(
        """
            DELETE FROM vertices
            WHERE graph = ?
            """.trimIndent()
    ).use { stmt ->
        stmt.setString(1, graphName)
        stmt.executeUpdate()
    }

    conn.prepareStatement(
        """
            DELETE FROM graphs
            WHERE name = ?
            """.trimIndent()
    ).use { stmt ->
        stmt.setString(1, graphName)
        stmt.executeUpdate()
    }
}

