package model

class DirectedGraph<V, K>(initVertex: Vertex<V>? = null) : Graph<V, K>{

	class DirectedVertex<V>(
		override var element: V, override val adjacencyList: MutableList<Vertex<V>> = mutableListOf()
	) : Vertex<V>

	class DirectedEdge<V, K>(
		var firstVertex: DirectedVertex<V>, var secondVertex: DirectedVertex<V>, override var key: K
	) : Edge<V, K> {
		override val pair: List<Vertex<V>> = listOf(firstVertex, secondVertex)
	}
	override var enterVertex = initVertex
	private val _vertices = HashMap<V, DirectedVertex<V>>()
	private val _edges = HashMap<K, DirectedEdge<V, K>>()

	override val vertices: Collection<DirectedVertex<V>>
		get() = _vertices.values

	override val edges: Collection<DirectedEdge<V, K>>
		get() = _edges.values

	override fun addEdge(firstVertex: V, secondVertex: V, key: K): Edge<V, K> {
		val firstV: DirectedVertex<V> = _vertices[firstVertex]
			?: throw NoSuchElementException("Vertex not found")
		val secondV = _vertices[secondVertex]
			?: throw NoSuchElementException("Vertex not found")
		firstV.adjacencyList.add(secondV)

		return _edges.getOrPut(key) { DirectedEdge(firstV, secondV, key) }
	}


	override fun addVertex(vertex: V) {
		val newVertex = DirectedVertex<V>(vertex)
		if (enterVertex == null) enterVertex = newVertex
		_vertices.getOrPut(vertex) { newVertex }
	}
}
