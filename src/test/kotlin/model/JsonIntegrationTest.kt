package model

import model.graph.Edge
import model.graph.Vertex
import model.utils.BellmanFordPathCalculator
import model.utils.CycleDetection
import model.utils.GraphPath
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import kotlin.io.path.createTempFile
import kotlin.io.path.pathString

class JsonIntegrationTest {

    /**
     * Test will do the following steps:
     *
     * - Apply BellmanFord before saving
     * - Save graph to Json
     * - Load graph from Json
     * - Apply BellmanFord again
     * - Assert paths are same
     */
    @RepeatedTest(10)
    fun `BellmanFord after loading graph from JSON`() {
        val randomGraph = RandomUndirectedIntGraph.get()
        val firstVertex = randomGraph.vertices.random().value
        val secondVertex = randomGraph.vertices.random().value

        val (predecessors, _)= BellmanFordPathCalculator.bellmanFordAlgorithm(randomGraph, firstVertex)

        val beforeJsonPath = GraphPath.construct(predecessors, secondVertex)
        val tempFile = createTempFile("graphTemp")
        JsonManager.saveJSON<Int, Int, Int>(tempFile.pathString, randomGraph)

        val loadedGraph = JsonManager.loadJSON<Int, Int, Int>(tempFile.pathString)
        val (newPredecessors, _)= BellmanFordPathCalculator.bellmanFordAlgorithm(loadedGraph, firstVertex)
        val afterJsonPath = GraphPath.construct(newPredecessors, secondVertex)
        assertTrue(afterJsonPath.size == beforeJsonPath.size)
        for (i in 0..(afterJsonPath.size - 1)) {
            assertTrue(afterJsonPath[i].weight == beforeJsonPath[i].weight)
            assertTrue(afterJsonPath[i].key == beforeJsonPath[i].key)
        }

    }
}