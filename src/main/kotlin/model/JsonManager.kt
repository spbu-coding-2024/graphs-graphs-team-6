package model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.graph.DirectedGraph
import model.graph.Edge
import model.graph.Graph
import model.graph.UndirectedGraph
import space.kscience.kmath.operations.Int16Ring
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.Int64Ring
import space.kscience.kmath.operations.Int8Ring
import space.kscience.kmath.operations.Ring
import java.io.File
import java.lang.Class

object JsonManager {
    val mapper = jacksonObjectMapper()

    private data class GraphMetadata(
        val isDirected: Boolean,
        val ring: String,
        val keyType: String,
        val vertexType: String,
        val weightType: String
    )

    private fun getRing(ringString: String): Ring<*> {
        return when(ringString) {
            "Int64Ring" -> Int64Ring
            "Int32Ring" -> Int32Ring
            "Int16Ring" -> Int16Ring
            "Int8Ring" -> Int8Ring
            else -> error("No such ring")
        }
    }

    /**
     * Loads graph from json file
     *      *
     * @param filePath selected file to load
     * @return A graph
     */
    @Suppress("UNCHECKED_CAST")
    fun <V: Any, K: Any, W: Comparable<W>>loadJSON(filePath: String): Graph<V, K, W> {
        val lines = File(filePath).readLines()
        val metadata = readJSON(lines[0], GraphMetadata::class.java)

        val (vertices, firstVertexOfEdges, secondVertexOfEdges, keysOfEdges, weightsOfEdges)
        = lines.subList(1, lines.size).map { readJSON(it, List::class.java) }

        val ring = getRing(metadata.ring) as Ring<W>
        val graph: Graph<V, K, W> = if (metadata.isDirected)
            DirectedGraph<V, K, W>(ring) else UndirectedGraph<V, K, W>(ring)

        for (v in vertices) {
            graph.addVertex(v as V)
        }
        for (i in 0..(firstVertexOfEdges.size - 1)) {
            graph.addEdge(
                firstVertexOfEdges[i] as V,
                secondVertexOfEdges[i] as V,
                keysOfEdges[i] as K,
                weightsOfEdges[i] as W)
        }
        return graph
    }

    private fun <T> writeJSON(value: T): String = mapper.writeValueAsString(value) + "\n"
    private fun <T> readJSON(json: String, type: Class<T>): T = mapper.readValue(json, type)
    /**
     * Saves graph to json file
     *
     * @param filePath file storage path
     * @param graph graph to save
     */
    fun <V: Any, K: Any, W: Comparable<W>>saveJSON(filePath: String, graph: Graph<V, K, W>) {
        val file = File(filePath)

        val metadata = writeJSON(GraphMetadata(
            graph is DirectedGraph,
            graph.ring::class.simpleName ?: error("No ring information about graph"),
            graph.edges.first().key::class.simpleName ?: error("No information about key type"),
            graph.vertices.first().value::class.simpleName ?: error("No information about vertex type"),
            graph.edges.first().weight::class.simpleName ?: error("No information about weight type"),
        ))

        val jsonVertices = writeJSON(graph.vertices.map { it.value }.toList())
        val jsonFirstEdges = writeJSON(graph.edges.map { it.startVertex.value }.toList())
        val jsonSecondEdges = writeJSON(graph.edges.map { it.endVertex.value }.toList())
        val jsonKeys = writeJSON(graph.edges.map { it.key }.toList())
        val jsonWeight = writeJSON(graph.edges.map { it.weight }.toList())

        return file.writeText(metadata + jsonVertices + jsonFirstEdges + jsonSecondEdges + jsonKeys + jsonWeight)
    }
}
