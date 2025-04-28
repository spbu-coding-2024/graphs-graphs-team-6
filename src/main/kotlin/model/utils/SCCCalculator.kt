package model.utils

import androidx.compose.ui.graphics.Color
import model.Graph
import model.Vertex
import model.utils.ColorUtils
import java.util.Stack

class SCCCalculator<V, K, W: Comparable<W>> {

	var onComputeListener: ((Map<Vertex<V, W>, Color>) -> Unit)? = null
	fun calculateComponents(graph: Graph<V, K, W>) {
		val sccs = tarjanSCC(graph)
		val colors = ColorUtils.assignColorsGrouped(sccs)
		onComputeListener?.invoke(colors)
	}

	private fun tarjanSCC(graph: Graph<V, K, W>): List<List<Vertex<V, W>>> {
		val indexMap = mutableMapOf<Vertex<V, W>, Int>()
		val lowLink  = mutableMapOf<Vertex<V, W>, Int>()
		val onStack  = mutableSetOf<Vertex<V, W>>()
		val stack    = Stack<Vertex<V, W>>()
		var index = 0
		val result = mutableListOf<List<Vertex<V, W>>>()

		fun dfs(v: Vertex<V, W>) {
			indexMap[v] = index
			lowLink[v] = index
			index++
			stack.push(v)
			onStack.add(v)

			v.adjacencyList.forEach { next ->
				val w = next.first
				if (!indexMap.containsKey(w)) {
					dfs(w)
					lowLink[v] = minOf(lowLink[v]!!, lowLink[w]!!)
				} else if (w in onStack) {
					lowLink[v] = minOf(lowLink[v]!!, indexMap[w]!!)
				}
			}

			if (lowLink[v] == indexMap[v]) {
				val component = mutableListOf<Vertex<V, W>>()
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