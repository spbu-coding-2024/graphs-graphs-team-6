package model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.graph.DirectedGraph
import model.graph.UndirectedGraph
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.io.TempDir
import space.kscience.kmath.operations.IntRing
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.io.path.name
import kotlin.io.path.pathString
import kotlin.io.path.writeText
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JsonManagerTest {

    @Test
    fun `saveJson try to save empty directed graph`() {
        val graph = DirectedGraph<Int, Int, Int>(IntRing)
        val path = createTempFile("tempGraph")
        try {
            JsonManager.saveJSON(path.pathString, graph)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message == "Failed requirement.")
        }
    }

    @Test
    fun `saveJson try to save empty undirected graph`() {
        val graph = UndirectedGraph<Int, Int, Int>(IntRing)
        val path = createTempFile("tempGraph")
        try {
            JsonManager.saveJSON(path.pathString, graph)
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message == "Failed requirement.")
        }
    }

    @Test
    fun `saveJson writes metadata `() {
        val graph = UndirectedGraph<Int, Int, Int>(IntRing).apply {
            addVertex(1)
            addVertex(2)
            addVertex(3)

            var index = 0
            addEdge(1, 2, index++, 5)
            addEdge(2, 3, index++, 5)
            addEdge(3, 1, index++, 5)
        }

        val path = createTempFile("tempGraph")
        Files.exists(path)
        JsonManager.saveJSON(path.pathString, graph)
        val lines = File(path.pathString).readLines()
        assertTrue(lines[0] == "{\"isDirected\":false,\"ring\":\"Int32Ring\",\"keyType\":\"Int\",\"vertexType\":\"Int\",\"weightType\":\"Int\"}")
    }

    @Test
    fun `saveJson writes data for a graph `() {
        val graph = UndirectedGraph<Int, Int, Int>(IntRing).apply {
            addVertex(1)
            addVertex(2)
            addVertex(3)

            var index = 0
            addEdge(1, 2, index++, 5)
            addEdge(2, 3, index++, 5)
            addEdge(3, 1, index++, 5)
        }

        val path = createTempFile("tempGraph")
        Files.exists(path)
        JsonManager.saveJSON(path.pathString, graph)
        val lines = File(path.pathString).readLines()
        assertTrue(lines[1] == "[1,2,3]")
        assertTrue(lines[2] == "[1,2,3]")
        assertTrue(lines[3] == "[2,3,1]")
    }

    @Test
    fun `loadJson loads undirected graph correctly`() {
        val path = createTempFile("tempGraph")
        path.writeText("""
            {"isDirected":true,"ring":"Int32Ring","keyType":"Int","vertexType":"String","weightType":"Int"}
            [1,2,3]
            [1,2,3]
            [2,3,1]
            [0,1,2]
            [5,5,5]
        """.trimIndent())
        Files.exists(path)
        val newGraph = JsonManager.loadJSON<Int, Int, Int>(path.pathString)
        assertTrue(newGraph.vertices.size == 3)
        assertTrue(newGraph.edges.size == 3)
        assertEquals(newGraph.vertices.map { it.value }, listOf(1,2,3))
        assertEquals(newGraph.edges.map { it.key }, listOf(0,1,2))
        assertEquals(newGraph.edges.map { it.weight }, listOf(5,5,5))
    }

    @RepeatedTest(25)
    fun `RepeatedTest for saveJson and loadJson`() {
        val path = createTempFile("tempGraph")
        Files.exists(path)

        val maxVertices = 1000
        val maxWeight = 100
        val numOfVertices = Random.nextInt(0, maxVertices)
        val numOfEdges = Random.nextInt(0, numOfVertices * (numOfVertices - 1) / 2 + 1)
        val randomGraph = UndirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..(numOfVertices - 1)) addVertex(i)

            for (i in 0..(numOfEdges - 1)) {
                val firstVert = Random.nextInt(0, numOfVertices - 1)
                val secondVert = Random.nextInt(0, numOfVertices - 1)
                val randomWeight = Random.nextInt(0, maxWeight)
                addEdge(firstVert, secondVert, i, randomWeight)
            }
        }
        JsonManager.saveJSON(path.pathString, randomGraph)

        val newGraph = JsonManager.loadJSON<Int, Int, Int>(path.pathString)
        assertTrue(newGraph.vertices.size == randomGraph.vertices.size)
        assertTrue(newGraph.edges.size == randomGraph.edges.size)
        assertEquals(newGraph.vertices.map { it.value }, randomGraph.vertices.map {it.value})
        assertEquals(newGraph.edges.map { it.key }, randomGraph.edges.map { it.key })
        assertEquals(newGraph.edges.map { it.weight }, randomGraph.edges.map { it.weight })
    }

}