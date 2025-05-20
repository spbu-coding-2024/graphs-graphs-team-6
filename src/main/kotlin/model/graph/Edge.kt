package model.graph

interface Edge<V, K, W : Comparable<W>> {
	var startVertex: Vertex<V>
	var endVertex: Vertex<V>
	val key: K
	var weight: W

	/**
	 * Return opposite vertex in edge
	 *
	 * If edge is a loop, return argument vertex
	 */
	fun opposite(value: V): V {
		return if (value == startVertex.value) endVertex.value else startVertex.value
	}

	/**
	 * Weight type cast to Float
	 *
	 * @throws IllegalStateException If type is unsupported
	 */
	fun weightToFloat(): Float {
		val num = weight
		when (num) {
			is Byte -> return num.toFloat()
			is Short -> return num.toFloat()
			is Int -> return num.toFloat()
			is Long -> return num.toFloat()
			else -> error("Type cannot be converted to float")
		}
	}
}
