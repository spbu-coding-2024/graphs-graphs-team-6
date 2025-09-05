package model.algos

import model.graph.Graph

class HarmonicCentrality<V : Any, K : Any, W : Comparable<W>> {
    fun calculate(graph: Graph<V, K, W>): HashMap<V, W>{
        val vertexCentrality: HashMap<V, W> = hashMapOf()
        for (startVertex in graph.vertices) {
            val (_, weights) = BellmanFordPathCalculator.bellmanFordAlgorithm(
                graph,
                startVertex.value
            )
            vertexCentrality[startVertex.value] = graph.ring.zero
            for (w in weights) {
                //vertexCentralityMap[startVertex.value] = graph.ring.zero or partial sum
                vertexCentrality[startVertex.value] = graph.ring.add(vertexCentrality[startVertex.value]!!, 1/w.value)
            }
        }
        return vertexCentrality
    }
}