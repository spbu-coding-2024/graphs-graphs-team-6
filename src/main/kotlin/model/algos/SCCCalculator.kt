package model.algos

import androidx.compose.ui.graphics.Color
import model.graph.Graph
import model.graph.Vertex
import viewmodel.ColorUtils
import java.util.Stack

/**
 * Algorithm fo searching Strongly Connected Components
 */
class SCCCalculator<V, K, W: Comparable<W>> {
	fun calculateComponents(graph: Graph<V, K, W>): Map<Vertex<V>, Color> {
		val sccs = tarjanSCC(graph)
		val colors = ColorUtils.assignColorsGrouped(sccs)
		return colors
	}

	private fun tarjanSCC(graph: Graph<V, K, W>): List<List<Vertex<V>>> {
		val indexMap = mutableMapOf<Vertex<V>, Int>()
		val lowLink = mutableMapOf<Vertex<V>, Int>()
		val onStack = mutableSetOf<Vertex<V>>()
		val stack = Stack<Vertex<V>>()
		var index = 0
		val result = mutableListOf<List<Vertex<V>>>()

		fun dfs(v: Vertex<V>) {
			indexMap[v] = index
			lowLink[v] = index
			index++
			stack.push(v)
			onStack.add(v)

			v.adjacencyList.forEach { w ->
				if (!indexMap.containsKey(w)) {
					dfs(w)
					lowLink[v] = minOf(
						lowLink[v] ?: error("Vertex is missing"),
						lowLink[w] ?: error("Vertex is missing")
					)
				} else if (w in onStack) {
					lowLink[v] = minOf(
						lowLink[v] ?: error("Vertex is missing"),
						indexMap[w] ?: error("Vertex is missing")
					)
				}
			}

			if (lowLink[v] == indexMap[v]) {
				val component = mutableListOf<Vertex<V>>()
				while (true) {
					val w = stack.pop()
					onStack.remove(w)
					component.add(w)
					if (w == v) break
				}
				result.add(component)
			}
		}

		graph.vertices.forEach { v ->
			if (!indexMap.containsKey(v)) dfs(v)
		}
		return result
	}
}
