package model.graph

import space.kscience.kmath.operations.Ring
import kotlin.collections.get


class UndirectedGraph<V, K, W : Comparable<W>>(override val ring: Ring<W>) : Graph<V, K, W> {

	class UndirectedVertex<V>(
		override var value: V,
		override val adjacencyList: MutableList<UndirectedVertex<V>> = mutableListOf()
	) : Vertex<V>

	data class UndirectedEdge<V, K, W : Comparable<W>>(
		override var startVertex: Vertex<V>,
		override var endVertex: Vertex<V>,
		override var key: K,
		override var weight: W
	) : Edge<V, K, W>

	private val _vertices = HashMap<V, UndirectedVertex<V>>()
	private val _edges = HashMap<K, UndirectedEdge<V, K, W>>()
	private val _edgeMap = HashMap<Pair<V, V>, K>()

	override val vertices: Collection<UndirectedVertex<V>>
		get() = _vertices.values

	override val edges: Collection<UndirectedEdge<V, K, W>>
		get() = _edges.values

	override fun getEdge(firstVertex: V, secondVertex: V): UndirectedEdge<V, K, W> {
		return _edges[_edgeMap[firstVertex to secondVertex]] ?: error("No such edge")
	}
	override fun addEdge(firstVertex: V, secondVertex: V, key: K, weight: W): Edge<V, K, W> {
		val firstV = _vertices[firstVertex]
			?: throw NoSuchElementException("Vertex not found")
		val secondV = _vertices[secondVertex]
			?: throw NoSuchElementException("Vertex not found")
		firstV.adjacencyList.add(secondV)
		secondV.adjacencyList.add(firstV)
		_edgeMap[firstVertex to secondVertex] = key
		_edgeMap[secondVertex to firstVertex] = key
		return _edges.getOrPut(key) {
			UndirectedEdge<V, K, W>(
				firstV,
				secondV,
				key,
				weight
			)
		}
	}

	override fun addVertex(vertex: V): Vertex<V> {
		return _vertices.getOrPut(vertex) { UndirectedVertex(vertex) }
	}

	override fun getVertex(vertex: V): Vertex<V> {
		return _vertices[vertex] ?: error("No such vertex with a given value")
	}
}
