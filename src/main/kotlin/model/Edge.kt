package model

interface Edge<V, K> {
    val pair: Collection<Vertex<V>>
    val key: K
}
