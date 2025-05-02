package model.utils

import androidx.compose.ui.graphics.Color
import model.Edge
import model.Graph
import model.Vertex
import viewmodel.ColorUtils
import kotlin.random.Random

class MSFFinder<V, K, W : Comparable<W>>(val graph: Graph<V, K, W>) {

	fun findMSF(): Map<Edge<V, K, W>, Color> {
		val msf = findMSFKruskal()
		val colors = ColorUtils.assignColorsGrouped(msf)
		return colors
	}

	val components = mutableListOf<MutableSet<Vertex<V>>>()
	private val visited = graph.vertices.associateWith { false }.toMutableMap()

	private fun dfs(v: Vertex<V>) {
		components.last().add(v)
		visited[v] = true
		for (u in v.adjacencyList) {
			if (!components.last().contains(u))
				dfs(u)
		}
	}

	init {
		graph.vertices.forEach {
			if (!(visited[it] ?: error("Vertex is missing"))) {
				components.add(mutableSetOf())
				dfs(it)
			}
		}
	}

	/**
	 * Finds the minimal spanning forest of the [Graph].
	 * @return List of pairs; each pair contains: list of edges sorted by weight of the current MST & sum of edge weights
	 */
	fun findMSFKruskal(): List<List<Edge<V, K, W>>> {
		val sortedEdges = graph.edges.sortedBy { it.weight }
		val res = mutableListOf<MutableList<Edge<V, K, W>>>()

		for (comp in components) {
			val mst = mutableListOf<Edge<V, K, W>>()
			val p = comp.associateWith { it }.toMutableMap()

			fun leaderDSU(v: Vertex<V>): Vertex<V> {
				return if (p[v] === v) v
				else leaderDSU(
					p[v] ?: error("DSU violation: $v vertex not found")
				).also { p[v] = it }
			}

			fun uniteDSU(a: Vertex<V>, b: Vertex<V>) {
				var aLeader = leaderDSU(a)
				var bLeader = leaderDSU(b)
				if (Random.Default.nextBoolean()) {
					aLeader = bLeader.also { bLeader = aLeader }
				}
				p[aLeader] = bLeader
			}

			for (edge in sortedEdges) {
				val v = edge.startVertex; val u = edge.endVertex
				val lv = leaderDSU(v)
				val lu = leaderDSU(u)
				if (lv != lu) {
					mst.add(edge)
					uniteDSU(v, u)
				}
			}
			res.add(mst)
		}
		return res
	}
}
