package model.sqlite

import java.sql.Connection
import java.sql.PreparedStatement

data class SQLiteWriteConnectionManager(val connection: Connection = createConnection()) {
    val metadataStmt: PreparedStatement = connection.prepareStatement(
        """
            INSERT INTO graphs
            (name, V, K, W, directed)
            VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
    )

    val vertexStmt: PreparedStatement = connection.prepareStatement(
        """
            INSERT INTO vertices
            (id, graph, value)
            VALUES (?, ?, ?)
            """.trimIndent()
    )

    val edgeStmt = connection.prepareStatement(
        """
            INSERT INTO edges 
            (id, graph, key, start_vertex, end_vertex, weight) 
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
    )

    fun setEdgeValues(
        id: String,
        graph: String,
        edge: EdgeRow
    ) {

        var substitutionIndex = 1
        edgeStmt.setString(substitutionIndex++, id)
        edgeStmt.setString(substitutionIndex++, graph)
        edgeStmt.setString(substitutionIndex++, edge.key)
        edgeStmt.setString(substitutionIndex++, edge.start)
        edgeStmt.setString(substitutionIndex++, edge.end)
        edgeStmt.setString(substitutionIndex, edge.weight)
        edgeStmt.addBatch()
    }

    fun setVertexValues(
        id: String,
        graphName: String,
        value: String
    ) {

        var substitutionIndex = 1

        vertexStmt.setString(substitutionIndex++, id)
        vertexStmt.setString(substitutionIndex++, graphName)
        vertexStmt.setString(substitutionIndex, value)
        vertexStmt.addBatch()
    }

    fun setMetadataValues(
        metadata: GraphMetadata
    ) {
        var substitutionIndex = 1
        metadataStmt.setString(substitutionIndex++, metadata.name)
        metadataStmt.setString(substitutionIndex++, metadata.typeV)
        metadataStmt.setString(substitutionIndex++, metadata.typeK)
        metadataStmt.setString(substitutionIndex++, metadata.typeW)
        metadataStmt.setBoolean(substitutionIndex, metadata.directed)
    }
}
