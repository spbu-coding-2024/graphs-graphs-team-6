package model

class DirectedGraph<V, K> : Graph<V, K> {

    data class DirectedVertex<V>(override var element: V) : Vertex<V>

    data class DirectedEdge<V, K>(
        var firstVertex: DirectedVertex<V>,
        var secondVertex: DirectedVertex<V>,
        override var key: K
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
        return _edges.getOrPut(key) {
            DirectedEdge<V, K>(
                DirectedVertex<V>(firstVertex),
                DirectedVertex<V>(secondVertex),
                key
            )
        }
    }

    override fun addVertex(vertex: V) {
        _vertices.getOrPut(vertex) { DirectedVertex<V>(vertex) }
    }

}
