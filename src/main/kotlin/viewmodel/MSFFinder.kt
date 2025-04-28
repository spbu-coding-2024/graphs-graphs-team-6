package viewmodel

import model.UndirectedGraph
import model.UndirectedGraph.UndirectedEdge
import model.UndirectedGraph.UndirectedVertex
import kotlin.random.Random

class MSFFinder<V, K, W : Comparable<W>>(val graph: UndirectedGraph<V, K, W>) {
	private val components = mutableListOf<MutableSet<UndirectedVertex<V>>>()
	private val belongsComponent = graph.vertices.associateWith { false }.toMutableMap()

	private fun dfs(v: UndirectedVertex<V>) {
		components.last().add(v)
		belongsComponent[v] = true
		for (u in v.adjacencyList) {
			if (!components.last().contains(u))
				dfs(u)
		}
	}

	/**
	 * Finds the minimal spanning forest of the [UndirectedGraph].
	 * @return List of pairs; each pair contains: list of edges sorted by weight of the current MST & sum of edge weights
	 */
	fun findMSFKruscal(): List<Pair<List<UndirectedEdge<V, K, W>>, W>> {
		graph.vertices.forEach {
			if (!(belongsComponent[it] ?: throw IllegalStateException("Vertex is missing"))) {
				components.add(mutableSetOf())
				dfs(it)
			}
		}

		val sortedEdges = graph.edges.sortedBy { it.weight }
		val res = mutableListOf<Pair<List<UndirectedEdge<V, K, W>>, W>>()

		for (comp in components) {
			val mst = mutableListOf<UndirectedEdge<V, K, W>>()
			val p = comp.associateWith { it }.toMutableMap()

			fun leaderDSU(v: UndirectedVertex<V>): UndirectedVertex<V> {
				return if (p[v] === v) v
				else leaderDSU(
					p[v] ?: throw IllegalStateException("DSU violation: $v vertex not found")
				).also { p[v] = it }
			}

			fun uniteDSU(a: UndirectedVertex<V>, b: UndirectedVertex<V>, edgeWeight: W) {
				var aLeader = leaderDSU(a)
				var bLeader = leaderDSU(b)
				if (Random.nextBoolean()) {
					aLeader = bLeader.also { bLeader = aLeader }
				}
				p[aLeader] = bLeader
			}

			var sumWeight = graph.ring.zero
			for (edge in graph.edges) {
				if ((comp intersect edge.pair).isEmpty())
					continue
				val (v, u) = edge.pair.toList()
				val lv = leaderDSU(v)
				val lu = leaderDSU(u)
				if (lv != lu) {
					mst.add(edge)
					uniteDSU(v, u, edge.weight)
					sumWeight = graph.ring.add(sumWeight, edge.weight)
				}
			}
			res.add(mst to sumWeight)
		}
		return res
	}
}
