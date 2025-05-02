package model

import space.kscience.kmath.operations.Ring

class DirectedGraph<V, K, W : Comparable<W>>(override val ring: Ring<W>) : Graph<V, K, W> {


	class DirectedVertex<V>(
		override var value: V, override val adjacencyList: MutableList<DirectedVertex<V>> = mutableListOf()
	) : Vertex<V>

	data class DirectedEdge<V, K, W : Comparable<W>>(
		override var startVertex: Vertex<V>,
		override var endVertex: Vertex<V>,
		override var key: K,
		override var weight: W
	) : Edge<V, K, W>

	private val _vertices = HashMap<V, DirectedVertex<V>>()
	private val _edges = HashMap<K, DirectedEdge<V, K, W>>()

	override val vertices: Collection<DirectedVertex<V>>
		get() = _vertices.values

	override val edges: Collection<DirectedEdge<V, K, W>>
		get() = _edges.values

	override fun addEdge(firstVertex: V, secondVertex: V, key: K, weight: W): Edge<V, K, W> {
		val firstV: DirectedVertex<V> = _vertices[firstVertex]
			?: throw NoSuchElementException("Vertex not found")
		val secondV = _vertices[secondVertex]
			?: throw NoSuchElementException("Vertex not found")
		firstV.adjacencyList.add(secondV)

		return _edges.getOrPut(key) { DirectedEdge(firstV, secondV, key, weight) }
	}

	override fun addVertex(vertex: V) {
		_vertices.getOrPut(vertex) { DirectedVertex(vertex) }
	}
}
