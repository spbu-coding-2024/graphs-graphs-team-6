package model

interface Edge<V, K, W> {
	val pair: Collection<Vertex<V>>
	val key: K
	var weight: W
}
