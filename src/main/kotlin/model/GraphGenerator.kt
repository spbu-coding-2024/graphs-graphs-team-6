package model

import model.graph.DirectedGraph
import model.graph.Graph
import model.graph.UndirectedGraph
import kotlin.random.Random

/**
 * An API to create random large graphs.
 */
object GraphGenerator {
	private val random = Random(System.currentTimeMillis())

	private fun expandGraph(graph: Graph<APPLICATION_V_TYPE, APPLICATION_K_TYPE, APPLICATION_W_TYPE>, vertexCount: Int, edgeProbability: Double) {
		for (i in 1..vertexCount) {
			graph.addVertex("v$i")
		}

		var edgeId = 0
		for (i in 1..vertexCount) {
			for (j in 1..vertexCount) {
				if (i != j && random.nextDouble() < edgeProbability) {
					val weight = random.nextInt(Constants.GENERATOR_WEIGHT_MIN, Constants.GENERATOR_WEIGHT_BOUND)
					graph.addEdge("v$i", "v$j", edgeId++, weight)
				}
			}
		}
	}

	/**
	 * Generates [UndirectedGraph] undirected graph with [vertexCount] vertices & with [edgeProbability] edge
	 * generation probability between them.
	 * @param vertexCount The number of vertices in the graph.
	 * @param edgeProbability The probability of appearing an edge between two vertices.
	 * This value must be not lower than 1 and not greater than 1.
	 */
	fun generateUndirectedGraph(
		vertexCount: Int = 1000,
		edgeProbability: Double = 0.01
	): UndirectedGraph<APPLICATION_V_TYPE, APPLICATION_K_TYPE, APPLICATION_W_TYPE> {
		require(edgeProbability in 0.0..1.0)
		val graph = UndirectedGraph<APPLICATION_V_TYPE, APPLICATION_K_TYPE, APPLICATION_W_TYPE>(APPLICATION_RING)
		expandGraph(graph, vertexCount, edgeProbability)
		return graph
	}

	/**
	 * Generates [UndirectedGraph] undirected graph with [vertexCount] vertices & with [edgeProbability] edge
	 * generation probability between them.
	 * @param vertexCount The number of vertices in the graph.
	 * @param edgeProbability The probability of appearing an edge between two vertices.
	 * This value must be not lower than 1 and not greater than 1.
	 */
	fun generateDirectedGraph(
		vertexCount: Int = 100,
		edgeProbability: Double = 0.01
	): DirectedGraph<APPLICATION_V_TYPE, APPLICATION_K_TYPE, APPLICATION_W_TYPE> {
		require(edgeProbability in 0.0..1.0)
		val graph = DirectedGraph<APPLICATION_V_TYPE, APPLICATION_K_TYPE, APPLICATION_W_TYPE>(APPLICATION_RING)
		expandGraph(graph, vertexCount, edgeProbability)
		return graph
	}
}
