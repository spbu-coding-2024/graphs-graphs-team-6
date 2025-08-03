package model.sqlite

import model.graph.Edge
import space.kscience.kmath.operations.ByteRing
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ShortRing
import java.sql.Connection
import java.sql.PreparedStatement

object SQLiteGraphOperations {
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

    fun writeGraphMetadata(
        connection: Connection, name: String, typeOfV: String, typeOfK: String, typeOfW: String, directedFlag: String
    ): PreparedStatement {
        val graphStmt = connection.prepareStatement(
            """
            INSERT INTO graphs
            (name, V, K, W, directed)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent())
        var substitutionIndex = 1

        graphStmt.setString(substitutionIndex++, name)
        graphStmt.setString(substitutionIndex++, typeOfV)
        graphStmt.setString(substitutionIndex++, typeOfK)
        graphStmt.setString(substitutionIndex++, typeOfW)
        graphStmt.setString(substitutionIndex, directedFlag)
        return graphStmt
    }

    fun writeVertex(
        connection: Connection, id: String, graphName: String, value: String
    ): PreparedStatement {
        val vertexStmt = connection.prepareStatement(
            """
            INSERT INTO vertices
            (id, graph, value)
            VALUES (?, ?, ?)
            """.trimIndent()
        )
        var substitutionIndex = 1

        vertexStmt.setString(substitutionIndex++, id)
        vertexStmt.setString(substitutionIndex++, graphName)
        vertexStmt.setString(substitutionIndex, value)
        return vertexStmt
    }


    fun <V, K, W : Comparable<W>> writeEdge(
        connection: Connection, graphName: String, e: Edge<V, K, W>
    ): PreparedStatement {
        val edgeStmt = connection.prepareStatement(
            """
            INSERT INTO edges 
            (id, graph, key, start_vertex, end_vertex, weight) 
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
        )

        var substitutionIndex = 1
        edgeStmt.setString(substitutionIndex++, graphName + "_" + e.key.toString())
        edgeStmt.setString(substitutionIndex++, graphName)
        edgeStmt.setString(substitutionIndex++, e.key.toString())
        edgeStmt.setString(substitutionIndex++, e.startVertex.value.toString())
        edgeStmt.setString(substitutionIndex++, e.endVertex.value.toString())
        edgeStmt.setString(substitutionIndex, e.weight.toString())
        return edgeStmt
    }
}
