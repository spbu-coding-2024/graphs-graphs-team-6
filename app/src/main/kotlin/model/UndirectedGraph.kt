package model

class UndirectedGraph<V, K> : Graph<V, K> {

    data class UndirectedVertex<V>(override var element: V) : Vertex<V>

    data class UndirectedEdge<V, K>(
        override var firstVertex: Vertex<V>,
        override var secondVertex: Vertex<V>,
        override var key: K
    ) : Edge<V, K>

    private var _vertices = HashMap<V, UndirectedVertex<V>>()
    private var _edges = HashMap<K, UndirectedEdge<V, K>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<V, K>>
        get() = _edges.values

    override fun addEdge(firstVertex: V, secondVertex: V, key: K): UndirectedEdge<V, K> {
        return _edges.getOrPut(key) { chooseVertexOrder(firstVertex, secondVertex, key) }
    }

    override fun addVertex(vertex: V) {
        _vertices.getOrPut(vertex) { UndirectedVertex(vertex) }
    }
    private fun chooseVertexOrder(firstVertex: V, secondVertex: V, key: K): UndirectedEdge<V, K> {
        var result = UndirectedEdge(
            UndirectedVertex(secondVertex),
            UndirectedVertex(firstVertex),
            key
        )
        var existsReverseEdge = false
        for (i in edges) {
            if (i == result) {
                existsReverseEdge = true
                break
            }
        }
        if (existsReverseEdge) result = UndirectedEdge(
            UndirectedVertex(firstVertex),
            UndirectedVertex(secondVertex),
            key)
        return result
    }

}