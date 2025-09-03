package model

import model.graph.Graph
import model.graph.UndirectedGraph
import model.sqlite.SQLiteManager
import model.sqlite.setupTables
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing
import java.sql.Connection
import java.sql.DriverManager

fun createMockConnection(): Connection {
    val conn = DriverManager.getConnection("jdbc:sqlite::memory:")
    setupTables(conn)

    return conn
}


fun <V : Any, K : Any, W : Comparable<W>> isGraphsEqual(first: Graph<V, K, W>, second: Graph<V, K, W>): Boolean {
    return first.vertices.all { firstVertex ->
        second.vertices.any { secondVertex ->
            firstVertex.value == secondVertex.value
        }
    } && second.vertices.all { secondVertex ->
        first.vertices.any { firstVertex ->
            firstVertex.value == secondVertex.value
        }
    } && first.edges.all { firstEdge ->
        second.edges.any { secondEdge ->
            firstEdge.startVertex == secondEdge.startVertex &&
                    firstEdge.endVertex == secondEdge.endVertex &&
                    firstEdge.key == secondEdge.key &&
                    firstEdge.weight == secondEdge.weight
        }
    } && second.edges.all { secondEdge ->
        first.edges.any { firstEdge ->
            firstEdge.startVertex == secondEdge.startVertex &&
                    firstEdge.endVertex == secondEdge.endVertex &&
                    firstEdge.key == secondEdge.key &&
                    firstEdge.weight == secondEdge.weight
        }
    } && first.vertices.size == second.vertices.size
            && first.edges.size == second.edges.size
}

@DisplayName("Tests for SQLite database")
class SQLiteDatabaseTest {
    private val manager = SQLiteManager(createMockConnection())
    val simpleGraph = UndirectedGraph<Int, Int, Int>(IntRing)
        .apply {
            addVertex(1)
            addVertex(2)
            addVertex(3)
            addEdge(1, 2, 1)
            addEdge(2, 1, 2)
            addEdge(1, 3, 3)
        }

    @Test
    fun emptyGraph() {
        val graph = UndirectedGraph<Int, Int, Int>(IntRing)
        manager.saveGraphToDatabase(graph, "emptyGraph")
        val received = manager.loadGraphFromDatabase<Int, Int, Int>("emptyGraph")
        assert(isGraphsEqual(received, graph))
    }

    @Test
    fun graphWithNoEdgesWriteRead() {
        val graph = UndirectedGraph<Int, Int, Int>(IntRing)
            .apply {
                addVertex(1)
                addVertex(2)
                addVertex(3)
            }
        manager.saveGraphToDatabase(graph, "graphWithNoEdgesWriteRead")
        val received = manager.loadGraphFromDatabase<Int, Int, Int>("graphWithNoEdgesWriteRead")
        assert(isGraphsEqual(received, graph))
    }

    @Test
    fun simpleGraphWriteRead() {
        manager.saveGraphToDatabase(simpleGraph, "simpleGraphWriteRead")
        val received = manager.loadGraphFromDatabase<Int, Int, Int>("simpleGraphWriteRead")
        assert(isGraphsEqual(received, simpleGraph))
    }

    @Test
    fun writeManyTimes() {
        manager.saveGraphToDatabase(simpleGraph, "writeManyTimes")
        manager.saveGraphToDatabase(simpleGraph, "writeManyTimes")
        manager.saveGraphToDatabase(simpleGraph, "writeManyTimes")
        val received = manager.loadGraphFromDatabase<Int, Int, Int>("writeManyTimes")
        assert(isGraphsEqual(received, simpleGraph))
    }

    @Test
    fun readManyTimes() {
        manager.saveGraphToDatabase(simpleGraph, "readManyTimes")
        var received = manager.loadGraphFromDatabase<Int, Int, Int>("readManyTimes")
        assert(isGraphsEqual(received, simpleGraph))

        received = manager.loadGraphFromDatabase("readManyTimes")
        assert(isGraphsEqual(received, simpleGraph))

        received = manager.loadGraphFromDatabase("readManyTimes")
        assert(isGraphsEqual(received, simpleGraph))
    }

    @Test
    fun writeReadManyTimes() {
        var graph = UndirectedGraph<Int, Int, Int>(IntRing)
            .apply {
                addVertex(1)
                addVertex(2)
                addVertex(3)
                addEdge(1, 2, 1)
                addEdge(2, 1, 2)
                addEdge(1, 3, 3)
            }
        manager.saveGraphToDatabase(graph, "writeReadManyTimes")
        var received = manager.loadGraphFromDatabase<Int, Int, Int>("writeReadManyTimes")
        assert(isGraphsEqual(received, graph))

        graph = UndirectedGraph<Int, Int, Int>(IntRing)
            .apply {
                addVertex(1)
                addVertex(2)
                addVertex(3)
                addEdge(1, 2, 1)
                addEdge(2, 1, 3)
                addEdge(2, 3, 2)
            }
        manager.saveGraphToDatabase(graph, "writeReadManyTimes")
        received = manager.loadGraphFromDatabase("writeReadManyTimes")
        assert(isGraphsEqual(received, graph))

        graph = UndirectedGraph<Int, Int, Int>(IntRing)
            .apply {
                addVertex(1)
                addVertex(2)
                addVertex(3)
                addEdge(1, 2, 3)
                addEdge(2, 2, 2)
                addEdge(1, 3, 1)
            }
        manager.saveGraphToDatabase(graph, "writeReadManyTimes")
        received = manager.loadGraphFromDatabase("writeReadManyTimes")
        assert(isGraphsEqual(received, graph))
    }
}
