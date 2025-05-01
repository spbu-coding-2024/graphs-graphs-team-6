package model

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import space.kscience.attributes.SafeType
import space.kscience.kmath.operations.Float64BufferOps.Companion.buffer
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.test.assertFailsWith
import model.UndirectedGraph.UndirectedEdge
import model.UndirectedGraph.UndirectedVertex

class LouvainTest {
	@Test
	fun `single vertex yields one community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val community = communities.single()
		assertEquals(1, community.size)
		assertEquals("A", community.single().element)
	}

	@Test
	fun `two disconnected vertices yield separate communities`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addVertex("B")
		val communities = Louvain(graph).detectCommunities()

		// Expect two separate communities for A and B
		assertEquals(1, communities.size)
		val labels = communities.flatten().map { it.element }.toSet()
		assertEquals(setOf("A", "B"), labels)
	}

	@Test
	fun `connected vertices yield one community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addVertex("B")
		graph.addEdge("A", "B", "e1", 1.0)
		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val community = communities.single()
		assertEquals(setOf("A", "B"), community.map { it.element }.toSet())
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

		graph.addVertex("C")

		graph.addEdge("D", "E", "e2", 1.0)

		val communities = Louvain(graph).detectCommunities()
		assertEquals(3, communities.size) // [A,B], [C], [D,E]

		val sortedCommunities = communities
			.map { it.map { v -> v.element }.sorted() }
			.sortedBy { it.first() }
		assertEquals(
			listOf(listOf("A", "B"), listOf("C"), listOf("D", "E")),
			sortedCommunities
		)
	}

	@Test
	fun `connected graph with multiple communities`() {

		val graph = UndirectedGraph<String, Int, Double>(Float64Field).apply {
			listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
				.forEach { addVertex(it) }

			var idx = 0

			addEdge("A", "B", idx++, 10.0)
			addEdge("A", "C", idx++, 10.0)
			addEdge("B", "C", idx++, 10.0)
			addEdge("B", "D", idx++, 10.0)
			addEdge("C", "D", idx++, 10.0)

			addEdge("E", "F", idx++, 10.0)
			addEdge("E", "G", idx++, 10.0)
			addEdge("F", "G", idx++, 10.0)
			addEdge("F", "H", idx++, 10.0)
			addEdge("G", "H", idx++, 10.0)

			addEdge("I", "J", idx++, 10.0)
			addEdge("I", "K", idx++, 10.0)
			addEdge("J", "K", idx++, 10.0)
			addEdge("J", "L", idx++, 10.0)
			addEdge("K", "L", idx++, 10.0)

			addEdge("D", "E", idx++, 1.0)   // 1 <-> 2
			addEdge("H", "I", idx++, 1.0)   // 2 <-> 3
			addEdge("L", "A", idx, 1.0)   // 3 <-> 1
		}

		val communities = Louvain(graph).detectCommunities().toList()
		assertEquals(3, communities.size)

		val sortedCommunities = communities
			.map { it.map { v -> v.element }.sorted() }
			.sortedBy { it.first() }
		assertEquals(
			listOf(listOf("A", "B", "C", "D"), listOf("E", "F", "G", "H"), listOf("I", "J", "K", "L")),
			sortedCommunities
		)
	}

	@Test
	fun `throws when weight is non-numeric`() {
		val stringRing = mockk<Ring<String>>()
		val graph = UndirectedGraph<String, String, String>(stringRing)

		graph.addVertex("A")
		graph.addVertex("B")
		graph.addEdge("A", "B", "e1", "nonNumeric")

		val exception = assertFailsWith<IllegalArgumentException> {
			Louvain(graph).detectCommunities()
		}
		assertTrue(exception.message!!.contains("Incompatible weight type"))
	}

	@Test
	fun `throws when second vertex mapping is missing`() {
		val graph = mockk<UndirectedGraph<String, String, Double>>()

		val vertexA = UndirectedVertex("A")

		every { graph.vertices } returns listOf(vertexA)
		val vertexB = UndirectedVertex("B")
		val edge = UndirectedEdge(setOf(vertexA, vertexB), "e1", 1.0)

		every { graph.edges } returns listOf(edge)

		val exception = assertFailsWith<IllegalStateException> {
			Louvain(graph).detectCommunities()
		}

		assertTrue(exception.message!!.contains("Vertex is missing"))
	}

	@Test
	fun `throws when first vertex mapping is missing`() {
		val graph = mockk<UndirectedGraph<String, String, Double>>()

		val vertexA = UndirectedVertex("A")

		every { graph.vertices } returns listOf(vertexA)
		val vertexB = UndirectedVertex("B")
		val edge = UndirectedEdge(setOf(vertexB, vertexA), "e1", 1.0)

		every { graph.edges } returns listOf(edge)

		val exception = assertFailsWith<IllegalStateException> {
			Louvain(graph).detectCommunities()
		}

		assertTrue(exception.message!!.contains("Vertex is missing"))
	}

	@Test
	fun `graph with self-loop yields single community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")
		graph.addEdge("A", "A", "loop", 2.0)

		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val community = communities.single()
		assertEquals(1, community.size)
		assertEquals("A", community.single().element)
	}
}
