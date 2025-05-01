package model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Ring

class LouvainTest {

	private class TestVertex<V>(override var element: V) : Vertex<V> {
		override val adjacencyList: MutableList<out Vertex<V>> = mutableListOf()
	}

	private class TestEdge<V, K, W : Comparable<W>>(
		private val v1: Vertex<V>,
		private val v2: Vertex<V>,
		override val key: K,
		override var weight: W
	) : Edge<V, K, W> {
		override val pair: Collection<Vertex<V>>
			get() = listOf(v1, v2)
	}

	private class TestGraph<V, K, W : Comparable<W>>(
		override val ring: Ring<W>
	) : Graph<V, K, W> {
		override val vertices: MutableList<Vertex<V>> = mutableListOf()
		override val edges: MutableList<Edge<V, K, W>> = mutableListOf()

		override fun addVertex(vertex: V) {
			vertices.add(TestVertex(vertex))
		}

		override fun addEdge(
			firstVertex: V,
			secondVertex: V,
			key: K,
			weight: W
		): Edge<V, K, W> {
			val v1 = vertices.firstOrNull { it.element == firstVertex }
				?: TestVertex(firstVertex).also { vertices.add(it) }
			val v2 = vertices.firstOrNull { it.element == secondVertex }
				?: TestVertex(secondVertex).also { vertices.add(it) }
			val edge = TestEdge(v1, v2, key, weight)
			edges.add(edge)
			return edge
		}
	}

	@Test
	fun `single vertex yields one community`() {
		val graph = TestGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val community = communities.single()
		assertEquals(1, community.size)
		assertEquals("A", community.single().element)
	}

	@Test
	fun `two disconnected vertices yield separate communities`() {
		val graph = TestGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addVertex("B")
		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val labels = communities.flatten().map { it.element }.toSet()
		assertEquals(setOf("A", "B"), labels)
	}

	@Test
	fun `connected vertices yield one community`() {
		val graph = TestGraph<String, String, Double>(Float64Field)
		graph.addEdge("A", "B", "e1", 1.0)
		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val community = communities.single()
		assertEquals(setOf("A", "B"), community.map { it.element }.toSet())
	}

	@Test
	fun `mixed graph yields correct number of communities`() {
		val graph = TestGraph<String, String, Double>(Float64Field)
		// component 1
		graph.addEdge("A", "B", "e1", 1.0)
		// isolated vertex
		graph.addVertex("C")
		// component 2
		graph.addEdge("D", "E", "e2", 1.0)

		val communities = Louvain(graph).detectCommunities()
		assertEquals(3, communities.size) // [A,B], [C], [D,E]

		val sortedCommunities = communities.map { it.map { v -> v.element }.sorted() }.sortedBy { it.first() }
		assertEquals(listOf(listOf("A", "B"), listOf("C"), listOf("D", "E")), sortedCommunities)
	}
}
