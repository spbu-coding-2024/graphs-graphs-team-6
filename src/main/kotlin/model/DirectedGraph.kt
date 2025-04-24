package model

class DirectedGraph<V, K> : Graph<V, K> {

	class DirectedVertex<V>(
		override var element: V, override val adjacencyList: MutableList<Vertex<V>> = mutableListOf()
	) : Vertex<V>

	class DirectedEdge<V, K>(
		var firstVertex: DirectedVertex<V>, var secondVertex: DirectedVertex<V>, override var key: K
	) : Edge<V, K> {
		override val pair: List<Vertex<V>> = listOf(firstVertex, secondVertex)
	}

	private val _vertices = HashMap<V, DirectedVertex<V>>()
	private val _edges = HashMap<K, DirectedEdge<V, K>>()

	override val vertices: Collection<DirectedVertex<V>>
		get() = _vertices.values

	override val edges: Collection<DirectedEdge<V, K>>
		get() = _edges.values

	override fun addEdge(firstVertex: V, secondVertex: V, key: K): Edge<V, K> {
		val firstV  = _vertices.getOrPut(firstVertex)  { DirectedVertex(firstVertex) }
		val secondV = _vertices.getOrPut(secondVertex) { DirectedVertex(secondVertex) }
		firstV.adjacencyList.add(secondV)

		return _edges.getOrPut(key) { DirectedEdge(firstV, secondV, key) }
	}


	override fun addVertex(vertex: V) {
		_vertices.getOrPut(vertex) { DirectedVertex<V>(vertex) }
	}

}
