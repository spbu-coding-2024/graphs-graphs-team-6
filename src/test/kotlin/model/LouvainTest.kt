package model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.Float64Field
import io.mockk.every
import io.mockk.mockk
import model.UndirectedGraph.UndirectedEdge
import model.UndirectedGraph.UndirectedVertex
import model.utils.Louvain
import space.kscience.kmath.operations.Ring

class LouvainTest {

	@Test
	fun `single vertex yields one community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		val communities = Louvain(graph).detectCommunities()

		Assertions.assertEquals(1, communities.size)
		val community = communities.single()
		val vertex = community.single()
		Assertions.assertEquals("A", vertex.value)
	}

	@Test
	fun `two disconnected vertices yield separate communities`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addVertex("B")

		val communities = Louvain(graph).detectCommunities()

		Assertions.assertEquals(1, communities.size)
	}

	@Test
	fun `connected vertices yield one community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addVertex("B")
		graph.addEdge("A", "B", "e1", 1.0)

		val communities = Louvain(graph).detectCommunities()
		Assertions.assertEquals(1, communities.size)

		val community = communities.single()
		val values = community.map { it.value }.toSet()
		Assertions.assertEquals(setOf("A", "B"), values)
	}

	@Test
	fun `mixed graph yields correct number of communities`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addVertex("B")
		graph.addVertex("C")
		graph.addVertex("D")
		graph.addVertex("E")

		graph.addEdge("A", "B", "e1", 1.0)
		graph.addEdge("D", "E", "e2", 1.0)

		val communities = Louvain(graph).detectCommunities()
		Assertions.assertEquals(3, communities.size)

		val sorted = communities
			.map { comm -> comm.map { it.value }.sorted() }
			.sortedBy { it.first() }

		Assertions.assertEquals(
			listOf(listOf("A", "B"), listOf("C"), listOf("D", "E")),
			sorted
		)
	}

	@Test
	fun `connected graph with multiple communities`() {
		val graph = UndirectedGraph<String, Int, Double>(Float64Field).apply {
			listOf("A","B","C","D","E","F","G","H","I","J","K","L").forEach { addVertex(it) }
			var idx = 0
			addEdge("A","B", idx++, 10.0)
			addEdge("A","C", idx++, 10.0)
			addEdge("B","C", idx++, 10.0)
			addEdge("B","D", idx++, 10.0)
			addEdge("C","D", idx++, 10.0)

			addEdge("E","F", idx++, 10.0)
			addEdge("E","G", idx++, 10.0)
			addEdge("F","G", idx++, 10.0)
			addEdge("F","H", idx++, 10.0)
			addEdge("G","H", idx++, 10.0)

			addEdge("I","J", idx++, 10.0)
			addEdge("I","K", idx++, 10.0)
			addEdge("J","K", idx++, 10.0)
			addEdge("J","L", idx++, 10.0)
			addEdge("K","L", idx++, 10.0)

			addEdge("D","E", idx++, 1.0)
			addEdge("H","I", idx++, 1.0)
			addEdge("L","A", idx, 1.0)
		}

		val communities = Louvain(graph).detectCommunities().toList()
		Assertions.assertEquals(3, communities.size)

		val sorted = communities
			.map { comm -> comm.map { it.value }.sorted() }
			.sortedBy { it.first() }

		Assertions.assertEquals(
			listOf(
				listOf("A","B","C","D"),
				listOf("E","F","G","H"),
				listOf("I","J","K","L")
			),
			sorted
		)
	}

	@Test
	fun `graph with self-loop yields single community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addEdge("A","A","loop", 2.0)

		val communities = Louvain(graph).detectCommunities()
		Assertions.assertEquals(1, communities.size)

		val vertex = communities.single().single()
		Assertions.assertEquals("A", vertex.value)
	}

	@Test
	fun `throws when weight is non-numeric`() {
		val stringRing = mockk<Ring<String>>()
		val graph = UndirectedGraph<String, String, String>(stringRing)
		graph.addVertex("A")
		graph.addVertex("B")
		graph.addEdge("A","B","e1","nonNumeric")

		Assertions.assertThrows(IllegalArgumentException::class.java) {
			Louvain(graph).detectCommunities()
		}
	}

	@Test
	fun `throws when second vertex mapping is missing`() {
		val graph = mockk<UndirectedGraph<String, String, Double>>()
		val vertexA = UndirectedVertex(1)
		every { graph.vertices } returns listOf(vertexA) as Collection<UndirectedVertex<String>>
		val vertexB = UndirectedVertex(2,)
		val edge = UndirectedEdge(vertexA, vertexB, "e1", 1.0)
		every { graph.edges } returns listOf(edge) as Collection<UndirectedEdge<String, String, Double>>

		Assertions.assertThrows(IllegalStateException::class.java) {
			Louvain(graph).detectCommunities()
		}
	}

	@Test
	fun `throws when first vertex mapping is missing`() {
		val graph = mockk<UndirectedGraph<String, String, Double>>()
		val vertexA = UndirectedVertex(1)
		every { graph.vertices } returns listOf(vertexA) as Collection<UndirectedVertex<String>>
		val vertexB = UndirectedVertex(2)
		val edge = UndirectedEdge(vertexA, vertexB, "e1", 1.0)
		every { graph.edges } returns listOf(edge) as Collection<UndirectedEdge<String, String, Double>>

		Assertions.assertThrows(IllegalStateException::class.java) {
			Louvain(graph).detectCommunities()
		}
	}
}
