package model.algos

import model.graph.Graph

class HarmonicCentrality<V : Any, K : Any, W : Comparable<W>> {
    fun calculate(graph: Graph<V, K, W>): HashMap<V, Double> {
        val vertexCentrality: HashMap<V, Double> = hashMapOf()
        for (startVertex in graph.vertices) {
            val (_, weights) = BellmanFordPathCalculator.bellmanFordAlgorithm(
                graph,
                startVertex.value
            )
            vertexCentrality[startVertex.value] = 0.0
            for (w in weights.filter { it.key != startVertex.value }) {
                //vertexCentralityMap[startVertex.value] = 0.0 or partial sum
                vertexCentrality[startVertex.value] = vertexCentrality[startVertex.value]!! + 1.0 / w.value.toString().toDouble()
            }
        }
        return vertexCentrality
    }
}