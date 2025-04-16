package model

interface Edge<V, K> {
    var firstVertex: Vertex<V>
    var secondVertex: Vertex<V>

    var key: K
}