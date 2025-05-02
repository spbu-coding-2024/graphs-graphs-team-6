package model.utils

import model.Edge
import model.Graph
import model.Vertex
import org.jetbrains.research.ictl.louvain.Link
import org.jetbrains.research.ictl.louvain.getPartition

class Louvain<V, K, W : Comparable<W>>(val graph: Graph<V, K, W>) {

	class EdgeWrapper<V, K, W : Comparable<W>>(
		val edge: Edge<V, K, W>,
		val vertexToID: Map<Vertex<V>, Int>
	) :
		Link {
		override fun source() = vertexToID[edge.pair.toList()[0]]
			?: error("Vertex is missing")
		override fun target() = vertexToID[edge.pair.toList()[if (edge.pair.size > 1) 1 else 0]]
			?: error("Vertex is missing")

		override fun weight() = toDouble(edge.weight)

		fun <W : Comparable<W>> toDouble(ringElem: W): Double =
			when (ringElem) {
				is Number -> ringElem.toDouble()
				else -> throw IllegalArgumentException(
					"Incompatible weight type"
				)
			}
	}

	fun detectCommunities(): Collection<Collection<Vertex<V>>> {
		val vertexToID = graph.vertices.withIndex().associate { it.value to it.index }
		val idToVertex = graph.vertices

		val links = graph.edges.map { EdgeWrapper(it, vertexToID) }
		val idToCommunity = getPartition(links, 1)
		val vertexToCommunity = idToVertex.associateWith { idToCommunity[vertexToID[it]] }

		val verticesGrouped = vertexToCommunity.keys.groupBy { vertexToCommunity[it] }.values
		return verticesGrouped
	}
}

