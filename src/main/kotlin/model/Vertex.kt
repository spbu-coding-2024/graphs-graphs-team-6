package model

interface Vertex<V> {
    var element: V
    val adjacencyList: MutableList<Vertex<V>>
}