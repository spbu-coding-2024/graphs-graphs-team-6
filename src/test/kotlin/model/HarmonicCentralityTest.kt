package model

import model.algos.HarmonicCentrality
import model.graph.DirectedGraph
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing

class HarmonicCentralityTest {
    @Test
    fun emptyGraph(){
        val graph: DirectedGraph<Int, Int, Int> = DirectedGraph(IntRing)
        val centralityMap = HarmonicCentrality<Int, Int, Int>().calculate(graph)
        assert(centralityMap.isEmpty())
    }
    @Test
    fun singleVertexGraph(){
        val graph: DirectedGraph<Int, Int, Int> = DirectedGraph<Int, Int, Int>(IntRing).apply {
            addVertex(0)
        }
        val centralityMap = HarmonicCentrality<Int, Int, Int>().calculate(graph)
        assert(centralityMap[0] == 0.0)
    }
    @Test
    fun singleEdgeGraph(){
        val graph: DirectedGraph<Int, Int, Int> = DirectedGraph<Int, Int, Int>(IntRing).apply {
            addVertex(0)
            addVertex(1)
            addEdge(0, 1, 0, 1)
        }
        val centralityMap = HarmonicCentrality<Int, Int, Int>().calculate(graph)
        assert(centralityMap[0] == 1.0)
        assert(centralityMap[1] == 0.0)
    }
}