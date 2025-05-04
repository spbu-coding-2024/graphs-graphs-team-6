package model

interface Vertex<V> {
    val id: Long
    var value: V
    val adjacencyList: MutableList<out Vertex<V>>
}
