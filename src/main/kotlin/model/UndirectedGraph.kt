package model


class UndirectedGraph<V, K> : Graph<V, K> {

    data class UndirectedVertex<V>(override var element: V) : Vertex<V>

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

    override val edges: Collection<Edge<V, K>>
        get() = _edges.values

    override fun addEdge(firstVertex: V, secondVertex: V, key: K): Edge<V, K> {
        return _edges.getOrPut(key) {
            UndirectedEdge<V, K>(
                setOf(UndirectedVertex(firstVertex), UndirectedVertex(secondVertex)),
                key
            )
        }
    }

    override fun addVertex(vertex: V) {
        _vertices.getOrPut(vertex) { UndirectedVertex<V>(vertex) }
    }

}
