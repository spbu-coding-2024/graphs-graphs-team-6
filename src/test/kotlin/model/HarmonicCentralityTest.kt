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
    @Test
    fun triangleGraph(){
        val graph: DirectedGraph<Int, Int, Int> = DirectedGraph<Int, Int, Int>(IntRing).apply {
            addVertex(0)
            addVertex(1)
            addVertex(2)
            addEdge(0, 1, 0, 1)
            addEdge(1, 2, 1, 1)
            addEdge(2, 0, 2, 1)
        }
        val centralityMap = HarmonicCentrality<Int, Int, Int>().calculate(graph)
        for (i in 0..2) {
            assert(centralityMap[i] == 1.5)
        }
    }
    @Test
    fun chainGraph(){
        val graph: DirectedGraph<Int, Int, Int> = DirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..3) {
                addVertex(i)
            }
            for (i in 0..2){
                addEdge(i, i+1, i, 1)
            }
        }
        val centralityMap = HarmonicCentrality<Int, Int, Int>().calculate(graph)
        assert(centralityMap[0] == 1.5 + 1.0/3.0)
        assert(centralityMap[1] == 1.5)
        assert(centralityMap[2] == 1.0)
        assert(centralityMap[3] == 0.0)
    }
    @Test
    fun starGraph(){
        val graph: DirectedGraph<Int, Int, Int> = DirectedGraph<Int, Int, Int>(IntRing).apply {
            for (i in 0..7) {
                addVertex(i)
            }
            for (i in 1..7) {
                addEdge(0, i, i, 1)
            }
        }
        val centralityMap = HarmonicCentrality<Int, Int, Int>().calculate(graph)
        assert(centralityMap[0] == 7.0)
        for (i in 1..7) {
            assert(centralityMap[i] == 0.0)
        }
    }
}
