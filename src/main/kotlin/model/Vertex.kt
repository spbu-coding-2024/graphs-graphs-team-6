package model

interface Vertex<V> {
    var value: V
    val adjacencyList: MutableList<out Vertex<V>>
}
