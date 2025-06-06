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
		return when (num) {
			is Byte -> num.toFloat()
			is Short -> num.toFloat()
			is Int -> num.toFloat()
			is Long -> num.toFloat()
			else -> error("Type cannot be converted to float")
		}
	}
}
