package model

import space.kscience.kmath.operations.Ring

interface Vertex<V, W: Comparable<W>> {
    var element: V
    val adjacencyList: MutableList<Pair<Vertex<V, W>, W>>
}
