package model

import model.graph.DirectedGraph
import model.graph.Graph
import model.graph.UndirectedGraph
import kotlin.random.Random
import space.kscience.kmath.operations.IntRing

object GraphGenerator {
	private val random = Random(System.currentTimeMillis())

	private fun expandGraph(graph: Graph<String, Int, Int>, vertexCount: Int, edgeProbability: Double) {
		for (i in 1..vertexCount) {
			graph.addVertex("v$i")
		}

		var edgeId = 0
		for (i in 1..vertexCount) {
			for (j in 1..vertexCount) {
				if (i != j && random.nextDouble() < edgeProbability) {
					val weight = random.nextInt(100)
					graph.addEdge("v$i", "v$j", edgeId++, weight)
				}
			}
		}
	}

	fun generateUndirectedGraph(vertexCount: Int = 1000, edgeProbability: Double = 0.01): UndirectedGraph<String, Int, Int> {
		require(edgeProbability in 0.0..1.0)
		val graph = UndirectedGraph<String, Int, Int>(IntRing)
		expandGraph(graph, vertexCount, edgeProbability)
		return graph
	}

	fun generateDirectedGraph(vertexCount: Int = 1000, edgeProbability: Double = 0.01): DirectedGraph<String, Int, Int> {
		require(edgeProbability in 0.0..1.0)
		val graph = DirectedGraph<String, Int, Int>(IntRing)
		expandGraph(graph, vertexCount, edgeProbability)
		return graph
	}
}