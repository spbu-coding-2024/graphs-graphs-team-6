package model

import space.kscience.kmath.operations.Ring

interface Graph<V, K, W: Comparable<W>>{
	val ring: Ring<W>

	/**
	 * A collection of vertices
	 */
	val vertices: Collection<Vertex<V>>
	/**
	 * A collection of edges
	 */
	val edges: Collection<Edge<V, K, W>>

	/**
	 * Adds edge to graph
	 *
	 * If edge already exist, don't overwrite it
	 *
	 * @param firstVertex First end point
	 * @param secondVertex Second end point
	 * @param key a key of edge
	 * @return Added edge
	 */
	fun addEdge(firstVertex: V, secondVertex: V, key: K, weight: W = ring.one): Edge<V, K, W>
	/**
	 * Adds vertex to graph
	 *
	 * If vertex already exist, don't overwrite it
	 *
	 * @param vertex A vertex to be added to a graph
	 * @return Added vertex
	 */
	fun addVertex(vertex: V): Vertex<V>

	/**
	 * Given two vertices, get corresponding edge
	 *
	 * @param firstVertex First end point
	 * @param secondVertex Second end point
	 * @return Corresponding edge
	 */
	fun getEdge(firstVertex: V, secondVertex: V): Edge<V, K, W>

	/**
	 * Given value of vertex, get its actual vertex
	 *
	 * @param vertex value of vertex
	 * @return Corresponding vertex
	 */
	fun getVertex(vertex: V): Vertex<V>

}
