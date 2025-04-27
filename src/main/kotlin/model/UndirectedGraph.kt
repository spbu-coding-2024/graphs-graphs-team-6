package model

import model.DirectedGraph.DirectedVertex


class UndirectedGraph<V, K>: Graph<V, K> {

	class UndirectedVertex<V>(
		override var element: V,
		override val adjacencyList: MutableList<Vertex<V>> = mutableListOf()
	) : Vertex<V>

	data class UndirectedEdge<V, K>(
		override val pair: Set<Vertex<V>>,
		override var key: K
	) : Edge<V, K> {
		init {
			require(pair.size == 2 || pair.size == 1)
			{ "Edge must connect 2 (or 1 if it is a loop) vertices" }
		}
	}

	private val _vertices = HashMap<V, UndirectedVertex<V>>()
	private val _edges = HashMap<K, UndirectedEdge<V, K>>()

	override val vertices: Collection<Vertex<V>>
		get() = _vertices.values

	override val edges: Collection<UndirectedEdge<V, K>>
		get() = _edges.values

	override fun addEdge(firstVertex: V, secondVertex: V, key: K): Edge<V, K> {
		val firstV = _vertices[firstVertex]
			?: throw NoSuchElementException("Vertex not found")
		val secondV = _vertices[secondVertex]
			?: throw NoSuchElementException("Vertex not found")
		firstV.adjacencyList.add(secondV)
		secondV.adjacencyList.add(firstV)
		return _edges.getOrPut(key) {
			UndirectedEdge<V, K>(
				setOf(firstV, secondV),
				key
			)
		}
	}

	override fun addVertex(vertex: V) {
		_vertices.getOrPut(vertex) { UndirectedVertex(vertex) }
	}
}
