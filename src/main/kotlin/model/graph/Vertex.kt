package model.graph

interface Vertex<V> {
    var value: V
    val adjacencyList: MutableList<out Vertex<V>>
}
