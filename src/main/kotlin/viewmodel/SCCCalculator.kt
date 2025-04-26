package viewmodel

import model.Graph
import model.Vertex
import androidx.compose.ui.graphics.Color
import java.awt.Color as AwtColor
import java.util.ArrayDeque

/**
 * Класс для вычисления компонент сильной связности (SCC) в ориентированном графе
 * и предоставления цвета для каждой вершины.
 * Вместо LiveData и ViewModel используется простой callback.
 */
class SCCCalculator<V, E> {

	/**
	 * Callback, который будет вызван после вычисления цветов.
	 * Передаёт отображение вершина -> compose-ui Color.
	 */
	var onComputeListener: ((Map<Vertex<V>, Color>) -> Unit)? = null

	/**
	 * Запускает вычисление компонент и генерирует цвета.
	 * Результат передаётся в [onComputeListener].
	 */
	fun calculateComponents(graph: Graph<V, E>) {
		val sccs = tarjanSCC(graph)
		val colors = assignColors(sccs)
		onComputeListener?.invoke(colors)
	}

	// --- Алгоритм Тарджана для SCC ---
	private fun tarjanSCC(graph: Graph<V, E>): List<List<Vertex<V>>> {
		val indexMap = mutableMapOf<Vertex<V>, Int>()
		val lowLink  = mutableMapOf<Vertex<V>, Int>()
		val onStack  = mutableSetOf<Vertex<V>>()
		val stack    = ArrayDeque<Vertex<V>>()
		var index = 0
		val result = mutableListOf<List<Vertex<V>>>()

		fun dfs(v: Vertex<V>) {
			indexMap[v] = index
			lowLink[v] = index
			index++
			stack.push(v)
			onStack.add(v)

			// Обход всех исходящих соседей через adjacencyList
			v.adjacencyList.forEach { w ->
				if (!indexMap.containsKey(w)) {
					dfs(w)
					lowLink[v] = minOf(lowLink[v]!!, lowLink[w]!!)
				} else if (w in onStack) {
					lowLink[v] = minOf(lowLink[v]!!, indexMap[w]!!)
				}
			}

			// Если вершина является корнем SCC — вытаскиваем компоненту из стека
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

	// --- Назначение цветов компонентам ---
	private fun assignColors(components: List<List<Vertex<V>>>): Map<Vertex<V>, Color> {
		val mapping = mutableMapOf<Vertex<V>, Color>()
		components.forEachIndexed { idx, comp ->
			val color = generateColor(idx)
			comp.forEach { node -> mapping[node] = color }
		}
		return mapping
	}

	// --- Генерация цвета через HSB и преобразование в compose-ui Color ---
	private fun generateColor(index: Int): Color {
		val hue = ((index * 137) % 360) / 360f  // нормализуем [0,1)
		val saturation = 0.7f
		val brightness = 0.9f
		val rgbInt = AwtColor.HSBtoRGB(hue, saturation, brightness)
		// HSBtoRGB возвращает ARGB в виде Int (0xAARRGGBB)
		return Color(rgbInt)
	}
}