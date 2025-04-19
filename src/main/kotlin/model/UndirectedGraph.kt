package model


class UndirectedGraph<V, K> : Graph<V, K> {

    data class UndirectedVertex<V>(override var element: V) : Vertex<V>

    data class UndirectedEdge<V, K>(
        private val firstVertex: UndirectedVertex<V>,
        private val secondVertex: UndirectedVertex<V>,
        override var key: K
    ) : Edge<V, K> {
        override val pair: Collection<Vertex<V>> = setOf(firstVertex, secondVertex)
    }

    private val _vertices = HashMap<V, UndirectedVertex<V>>()
    private val _edges = HashMap<K, UndirectedEdge<V, K>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<V, K>>
        get() = _edges.values

    override fun addEdge(firstVertex: V, secondVertex: V, key: K): Edge<V, K> {
        return _edges.getOrPut(key) {
            UndirectedEdge<V, K>(
                UndirectedVertex<V>(firstVertex),
                UndirectedVertex<V>(secondVertex),
                key
            )
        }
    }

    override fun addVertex(vertex: V) {
        _vertices.getOrPut(vertex) { UndirectedVertex<V>(vertex) }
    }


}