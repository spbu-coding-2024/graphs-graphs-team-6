package model

class DirectedGraph<V, K> : Graph<V, K> {

    data class DirectedVertex<V>(override var element: V) : Vertex<V>

    data class DirectedEdge<V, K>(
        override var firstVertex: Vertex<V>,
        override var secondVertex: Vertex<V>,
        override var key: K
    ) : Edge<V, K>

    private var _verticies = HashMap<V, DirectedVertex<V>>()
    private var _edges = HashMap<K, DirectedEdge<V, K>>()

    override val vertices: Collection<Vertex<V>>
        get() = _verticies.values

    override val edges: Collection<Edge<V, K>>
        get() = _edges.values

    override fun addEdge(firstVertex: V, secondVertex: V, element: K): Edge<V, K> {
        return _edges.getOrPut(element) {
            DirectedEdge<V, K>(
                DirectedVertex<V>(firstVertex),
                DirectedVertex<V>(secondVertex),
                element
            )
        }
    }

    override fun addVertex(vertex: V) {
        _edges.getOrPut(vertex) { DirectedVertex<V>(vertex) }
    }

}