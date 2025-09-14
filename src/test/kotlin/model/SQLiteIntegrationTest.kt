package model

import model.algos.HarmonicCentrality
import model.graph.DirectedGraph
import model.json.JsonManager
import model.sqlite.SQLiteManager
import model.sqlite.createConnection
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing
import java.io.File

class SQLiteIntegrationTest {

    /**
     * Tests will do the following steps on fixed and random graphs:
     *
     * - Apply HarmonicCentrality before saving
     * - Save graph to SQLite
     * - Load graph from SQLite
     * - Apply HarmonicCentrality again
     * - Assert values are same
     */
    @Test
    fun constantSQLiteIntegrationTest(){
        val graphBefore = DirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..10) {
                addVertex(i)
            }
            for (i in 0..5){
                addEdge(i, 2*i, i, 2*(5 - i) + 1)
            }
        }
        runTestTemplate(graphBefore)
    }

    @RepeatedTest(10)
    fun randomSQLiteIntegrationTest(){
        val randomGraph = GraphGenerator.generateDirectedGraph()
        try {
            runTestTemplate(randomGraph)
        } catch (e: Exception) {
            val errorTime = System.currentTimeMillis()
            val file = File("${Constants.JSON_DEFAULT_DIRECTORY}/$errorTime-error_graph.json")
            file.parentFile?.mkdirs()
            JsonManager.saveJSON("${Constants.JSON_DEFAULT_DIRECTORY}/$errorTime-error_graph.json", randomGraph)
            throw e
        }
    }

    fun <V: Any, K: Any, W: Comparable<W>>runTestTemplate(graphBefore: DirectedGraph<V, K, W>){

        val centralityBefore = HarmonicCentrality<V, K, W>().calculate(graphBefore)

        val connection = createConnection()
        val sqlite = SQLiteManager(connection)
        sqlite.saveGraphToDatabase(graphBefore, "SQLiteIntegrationTest")
        val graphAfter = sqlite.loadGraphFromDatabase<V, K, W>("SQLiteIntegrationTest")
        connection.close()

        val centralityAfter = HarmonicCentrality<V, K, W>().calculate(graphAfter)

        assert(graphBefore.vertices.size == graphAfter.vertices.size)
        assert(graphBefore.edges.size == graphAfter.edges.size)
        assert(centralityAfter.all { centralityBefore[it.key] == it.value })
    }
}
