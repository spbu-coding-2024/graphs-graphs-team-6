package model.algos

import model.graph.Graph

class HarmonicCentrality<V : Any, K : Any, W : Comparable<W>> {
    fun calculate(graph: Graph<V, K, W>): HashMap<V, Double> {
        val centrality: HashMap<V, Double> = hashMapOf()
        for (startVertex in graph.vertices) {
            val vertex = startVertex.value
            val (_, weights) = BellmanFordPathCalculator.bellmanFordAlgorithm(
                graph,
                vertex
            )
            centrality[vertex] = 0.0
            for (w in weights.filter { it.key != startVertex.value }) {
                //vertexCentralityMap[startVertex.value] = 0.0 or partial sum
                centrality[vertex] = centrality[vertex]!! + 1.0 / w.value.toString().toDouble()
            }
        }
        return centrality
    }
}
