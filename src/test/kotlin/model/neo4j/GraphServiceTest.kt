package model.neo4j

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.graph.DirectedGraph
import model.graph.UndirectedGraph
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.neo4j.ogm.session.Session
import org.neo4j.ogm.session.SessionFactory
import space.kscience.kmath.operations.IntRing

class GraphServiceTest {
 @BeforeEach
 fun setup() {
  GraphService.sessionFactory = null
 }

 @Test
 fun `loadGraph throws when sessionFactory is not initialized`() {
  Assertions.assertThrows(IllegalStateException::class.java) {
   GraphService.loadGraph<Int, Int, Int>(isDirected = true)
  }
 }

 @Test
 fun `saveGraph throws when sessionFactory is not initialized`() {
  val graph = DirectedGraph<Int, Int, Int>(IntRing)
  Assertions.assertThrows(IllegalStateException::class.java) {
   GraphService.saveGraph(graph)
  }
 }

 @Test
 fun `loadGraph returns empty directed graph`() {
  val factory = mockk<SessionFactory>()
  val session = mockk<Session>()
  every { factory.openSession() } returns session
  every { session.loadAll(EdgeEntity::class.java) } returns emptyList()
  every { session.loadAll(VertexEntity::class.java) } returns emptyList()
  every { session.clear() } returns Unit
  GraphService.sessionFactory = factory

  val graph = GraphService.loadGraph<Int, Int, Int>(isDirected = true)

  Assertions.assertTrue(graph is DirectedGraph<*, *, *>)
  Assertions.assertTrue(graph.vertices.isEmpty())
  Assertions.assertTrue(graph.edges.isEmpty())
  verify(exactly = 1) { factory.openSession() }
 }

 @Test
 fun `loadGraph constructs graph with one vertex and self-loop`() {
  val factory = mockk<SessionFactory>()
  val session = mockk<Session>()
  val vert = VertexEntity().apply {
   id = 1L
   dataType = Int::class.java.name
   dataJson = GraphService.toJson(42)
  }
  // И одно петлевое ребро
  val edge = EdgeEntity().apply {
   start = vert
   end = vert
   keyType = Int::class.java.name
   keyJson = GraphService.toJson(7)
   weightType = Int::class.java.name
   weightJson = GraphService.toJson(5)
  }

  every { factory.openSession() } returns session
  every { session.clear() } returns Unit
  every { session.loadAll(EdgeEntity::class.java) } returns listOf(edge)
  every { session.loadAll(VertexEntity::class.java) } returns listOf(vert)
  GraphService.sessionFactory = factory

  val graph = GraphService.loadGraph<Int, Int, Int>(isDirected = false)

  Assertions.assertTrue(graph is UndirectedGraph)
  Assertions.assertEquals(1, graph.vertices.size)
  Assertions.assertEquals(1, graph.edges.size)
  val v = graph.vertices.single()
  Assertions.assertEquals(42, v.value)
  val e = graph.edges.single()
  Assertions.assertEquals(7, e.key)
  Assertions.assertEquals(5, e.weight)
 }

 @Test
 fun `saveGraph calls session methods`() {
  val factory = mockk<SessionFactory>()
  // делаем Session relaxed, чтобы не писать ever’y для query, save, clear и т.п.
  val session = mockk<Session>(relaxed = true)
  every { factory.openSession() } returns session
  GraphService.sessionFactory = factory

  val graph = UndirectedGraph<Int, String, Int>(IntRing).apply {
   addVertex(1)
   addVertex(2)
   addEdge(1, 2, "k", 5)
  }
  GraphService.saveGraph(graph)

  verify(exactly = 1) { session.query("MATCH (n) DETACH DELETE n", emptyMap<String, Any>()) }
  verify(exactly = 2) { session.save(ofType(VertexEntity::class)) }
  verify(exactly = 1) { session.save(ofType(EdgeEntity::class)) }
  verify(exactly = 1) { session.clear() }
 }
}
