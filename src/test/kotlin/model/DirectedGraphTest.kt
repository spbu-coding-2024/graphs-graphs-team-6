package model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows

class DirectedGraphTest {
 private lateinit var graph: DirectedGraph<String, String>

 @BeforeEach
 fun setup() {
  graph = DirectedGraph()
 }

 @Test
 fun testOneVertex() {
  graph.addVertex("A")
  assertEquals(1, graph.vertices.size)
  val vertex = graph.vertices.first()
  assertEquals("A", vertex.element)
 }

 @Test
 fun testTwoVerticesOneEdge() {
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addEdge("A", "B", "e1")
  assertEquals(2, graph.vertices.size)
  val vA = graph.vertices.first { it.element == "A" }
  val vB = graph.vertices.first { it.element == "B" }
  assertTrue(vA.adjacencyList.contains(vB))
  assertFalse(vB.adjacencyList.contains(vA))
 }

 @Test
 fun testEdgeNonExistent() {
  assertThrows<NoSuchElementException> {
   graph.addEdge("X", "Y", "e")
  }
  graph.addVertex("A")
  assertThrows<NoSuchElementException> {
   graph.addEdge("A", "Y", "e")
  }
 }

 @Test
 fun testLoop() {
  graph.addVertex("A")
  graph.addEdge("A", "A", "loop")
  assertEquals(1, graph.vertices.size)
  val vA = graph.vertices.first { it.element == "A" }
  assertTrue(vA.adjacencyList.contains(vA))
 }
}
