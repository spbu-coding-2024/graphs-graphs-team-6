package model

interface Graph<V, K> {
    /**
     * A collection of vertices
     */
    val vertices: Collection<Vertex<V>>
    /**
     * A collection of edges
     */
    val edges: Collection<Edge<V, K>>

    /**
     * Adds edge to graph
     *
     * If edge already exist, overwrite it
     *
     * @param firstVertex First end point
     * @param secondVertex Second end point
     * @param key a key of edge
     * @return Added edge
     */
    fun addEdge(firstVertex: V, secondVertex: V, key: K): Edge<V, K>
    /**
     * Adds vertex to graph
     *
     * If vertex already exist, overwrite it
     *
     * @param vertex A vertex to be added to a graph
     */
    fun addVertex(vertex: V)
}