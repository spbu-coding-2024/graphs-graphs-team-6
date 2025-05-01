package model

interface Edge<V, K, W: Comparable<W>> {
	val pair: Collection<Vertex<V>>
	val key: K
	var weight: W
	/*
	 * Return opposite vertex in edge
	 *
	 * If edge is a loop, return argument vertex
	 */
	fun opposite(vertex: V): V {
		this.pair.forEach {
			if (it.element != vertex) return it.element
		}
		return vertex
	}
}
