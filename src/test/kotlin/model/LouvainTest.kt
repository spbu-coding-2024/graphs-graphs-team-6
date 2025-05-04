package model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Ring
import kotlin.test.assertFailsWith
import io.mockk.every
import io.mockk.mockk
import model.UndirectedGraph.UndirectedEdge
import model.UndirectedGraph.UndirectedVertex
import model.utils.Louvain

class LouvainTest {

	@Test
	fun `single vertex yields one community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")  // id == 1
		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
		val community = communities.single()
		with(community.single()) {
			assertEquals(1L, id)
			assertEquals("A", value)
		}
	}

	@Test
	fun `two disconnected vertices yield separate communities`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")  // id == 1
		graph.addVertex("B")  // id == 2

		val communities = Louvain(graph).detectCommunities()

		assertEquals(1, communities.size)
	}

	@Test
	fun `connected vertices yield one community`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")  // id == 1
		graph.addVertex("B")  // id == 2
		graph.addEdge("A", "B", "e1", 1.0)

		val communities = Louvain(graph).detectCommunities()
		assertEquals(1, communities.size)

		val community = communities.single()
		val values = community.map { it.value }.toSet()
		val ids = community.map { it.id }.toSet()
		assertEquals(setOf("A", "B"), values)
		assertEquals(setOf(1L, 2L), ids)
	}

	@Test
	fun `mixed graph yields correct number of communities`() {
		val graph = UndirectedGraph<String, String, Double>(Float64Field)
		graph.addVertex("A")  // 1
		graph.addVertex("B")  // 2
		graph.addVertex("C")  // 3
		graph.addVertex("D")  // 4
		graph.addVertex("E")  // 5

		graph.addEdge("A", "B", "e1", 1.0)
		// C останется несвязной
		graph.addEdge("D", "E", "e2", 1.0)

		val communities = Louvain(graph).detectCommunities()
		assertEquals(3, communities.size)  // [A,B], [C], [D,E]

		// Для читаемости приведём к списку списков значений
		val sorted = communities
			.map { comm ->
				comm.map { it.value }.sorted()
			}
			.sortedBy { it.first() }

		assertEquals(
			listOf(listOf("A", "B"), listOf("C"), listOf("D", "E")),
			sorted
		)
	}

	@Test
	fun `connected graph with multiple communities`() {
		val graph = UndirectedGraph<String, Int, Double>(Float64Field).apply {
			listOf("A","B","C","D","E","F","G","H","I","J","K","L")
				.forEach { addVertex(it) }
			var idx=0
			// три «кластера» по 4 вершины
			addEdge("A","B", idx++,10.0)
			addEdge("A","C", idx++,10.0)
			addEdge("B","C", idx++,10.0)
			addEdge("B","D", idx++,10.0)
			addEdge("C","D", idx++,10.0)

			addEdge("E","F", idx++,10.0)
			addEdge("E","G", idx++,10.0)
			addEdge("F","G", idx++,10.0)
			addEdge("F","H", idx++,10.0)
			addEdge("G","H", idx++,10.0)

			addEdge("I","J", idx++,10.0)
			addEdge("I","K", idx++,10.0)
			addEdge("J","K", idx++,10.0)
			addEdge("J","L", idx++,10.0)
			addEdge("K","L", idx++,10.0)

			// слабые связи между кластерами
			addEdge("D","E", idx++,1.0)
			addEdge("H","I", idx++,1.0)
			addEdge("L","A", idx,1.0)
		}

		val communities = Louvain(graph).detectCommunities().toList()
		assertEquals(3, communities.size)

		val sorted = communities
			.map { comm -> comm.map { it.value }.sorted() }
			.sortedBy { it.first() }

		assertEquals(
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
		graph.addVertex("A")       // id == 1
		graph.addEdge("A","A","loop",2.0)

		val communities = Louvain(graph).detectCommunities()
		assertEquals(1, communities.size)

		with(communities.single().single()) {
			assertEquals(1L, id)
			assertEquals("A", value)
		}
	}

	@Test
	fun `throws when weight is non-numeric`() {
		val stringRing = mockk<Ring<String>>()
		val graph = UndirectedGraph<String, String, String>(stringRing)
		graph.addVertex("A")
		graph.addVertex("B")
		graph.addEdge("A","B","e1","nonNumeric")

		val ex = assertFailsWith<IllegalArgumentException> {
			Louvain(graph).detectCommunities()
		}
		assertTrue(ex.message!!.contains("Incompatible weight type"))
	}

	@Test
	fun `throws when second vertex mapping is missing`() {
		val graph = mockk<UndirectedGraph<String, String, Double>>()
		val vertexA = UndirectedVertex(1, "A")
		every { graph.vertices } returns listOf(vertexA)
		val vertexB = UndirectedVertex(2,"B")
		val edge = UndirectedEdge(vertexA, vertexB, "e1", 1.0)
		every { graph.edges } returns listOf(edge)

		val ex = assertFailsWith<IllegalStateException> {
			Louvain(graph).detectCommunities()
		}
		assertTrue(ex.message!!.contains("Vertex is missing"))
	}

	@Test
	fun `throws when first vertex mapping is missing`() {
		val graph = mockk<UndirectedGraph<String, String, Double>>()
		val vertexA = UndirectedVertex(1, "A")
		every { graph.vertices } returns listOf(vertexA)
		val vertexB = UndirectedVertex(2, "B")
		val edge = UndirectedEdge(vertexA, vertexB, "e1", 1.0)
		every { graph.edges } returns listOf(edge)

		val ex = assertFailsWith<IllegalStateException> {
			Louvain(graph).detectCommunities()
		}
		assertTrue(ex.message!!.contains("Vertex is missing"))
	}
}
