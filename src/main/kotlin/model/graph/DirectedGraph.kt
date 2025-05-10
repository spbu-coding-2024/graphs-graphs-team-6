package model.graph

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import space.kscience.kmath.operations.Ring
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.get

class DirectedGraph<V, K, W : Comparable<W>>(override val ring: Ring<W>) : Graph<V, K, W> {
	//var idCounter = AtomicLong(0)

	class DirectedVertex<V>(
		override var value: V,
		override val adjacencyList: MutableList<DirectedVertex<V>> = mutableListOf()
	) : Vertex<V>

	data class DirectedEdge<V, K, W : Comparable<W>>(
		override var startVertex: Vertex<V>,
		override var endVertex: Vertex<V>,
		override var key: K,
		override var weight: W
	) : Edge<V, K, W>

	private val _vertices = HashMap<V, DirectedVertex<V>>()

	private val _edges = HashMap<K, DirectedEdge<V, K, W>>()

	private val _edgeMap = HashMap<Pair<V, V>, K>()

	override val vertices: Collection<DirectedVertex<V>>
		get() = _vertices.values

	override val edges: Collection<DirectedEdge<V, K, W>>
		get() = _edges.values

	override fun getEdge(firstVertex: V, secondVertex: V): DirectedEdge<V, K, W> {
		return _edges[_edgeMap[firstVertex to secondVertex]] ?: error("No such edge")
	}

	override fun addEdge(firstVertex: V, secondVertex: V, key: K, weight: W): Edge<V, K, W> {
		val firstV: DirectedVertex<V> = _vertices[firstVertex]
			?: throw NoSuchElementException("Vertex not found")
		val secondV = _vertices[secondVertex]
			?: throw NoSuchElementException("Vertex not found")
		firstV.adjacencyList.add(secondV)
		_edgeMap[firstVertex to secondVertex] = key
		return _edges.getOrPut(key) { DirectedEdge(firstV, secondV, key, weight) }
	}

	override fun addVertex(vertex: V): Vertex<V> {
		return _vertices.getOrPut(vertex) { DirectedVertex(vertex) }
	}

	override fun getVertex(vertex: V): Vertex<V> {
		return _vertices[vertex] ?: error("No such vertex with a given value")
	}
}
