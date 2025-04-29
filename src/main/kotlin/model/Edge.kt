package model

interface Edge<V, K, W: Comparable<W>> {
	val pair: Collection<Vertex<V>>
	val key: K
	var weight: W
}
