package viewmodel

import model.Graph
import model.Vertex
import androidx.compose.ui.graphics.Color
import java.awt.Color as AwtColor
import java.util.ArrayDeque
import java.util.Stack

private const val CIRCLE_GRADUS = 360
private const val SATURATION = 0.7f
private const val BRIGHTNESS = 0.9f
private const val INDEX_MULITPLIER = 137

class SCCCalculator<V, K, W: Comparable<W>> {

	var onComputeListener: ((Map<Vertex<V>, Color>) -> Unit)? = null
	fun calculateComponents(graph: Graph<V, K, W>) {
		val sccs = tarjanSCC(graph)
		val colors = assignColors(sccs)
		onComputeListener?.invoke(colors)
	}

	private fun tarjanSCC(graph: Graph<V, K, W>): List<List<Vertex<V>>> {
		val indexMap = mutableMapOf<Vertex<V>, Int>()
		val lowLink  = mutableMapOf<Vertex<V>, Int>()
		val onStack  = mutableSetOf<Vertex<V>>()
		val stack    = Stack<Vertex<V>>()
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
					lowLink[v] = minOf(lowLink[v]!!, lowLink[w]!!)
				} else if (w in onStack) {
					lowLink[v] = minOf(lowLink[v]!!, indexMap[w]!!)
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

	private fun assignColors(components: List<List<Vertex<V>>>): Map<Vertex<V>, Color> {
		val mapping = mutableMapOf<Vertex<V>, Color>()
		components.forEachIndexed { idx, comp ->
			val color = generateColor(idx)
			comp.forEach { node -> mapping[node] = color }
		}
		return mapping
	}

	private fun generateColor(index: Int): Color {
		val hue = ((index * INDEX_MULITPLIER) % CIRCLE_GRADUS) / (CIRCLE_GRADUS).toFloat()
		val rgbInt = AwtColor.HSBtoRGB(hue, SATURATION, BRIGHTNESS)
		return Color(rgbInt)
	}
}
