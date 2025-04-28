package model

import space.kscience.kmath.operations.Ring

interface Edge<V, K, W: Comparable<W>> {
	val pair: Collection<Vertex<V, W>>
	val key: K
	var weight: W
}
