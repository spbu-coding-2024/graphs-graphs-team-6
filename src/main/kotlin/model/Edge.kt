package model

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
}
