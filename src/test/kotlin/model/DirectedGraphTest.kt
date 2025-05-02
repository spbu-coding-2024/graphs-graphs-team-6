package model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import space.kscience.kmath.operations.IntRing

class DirectedGraphTest {
 private lateinit var graph: DirectedGraph<String, String, Int>

 @BeforeEach
 fun setup() {
  graph = DirectedGraph(IntRing)
 }

 @Test
 fun testOneVertex() {
  graph.addVertex("A")
  assertEquals(1, graph.vertices.size)
  val vertex = graph.vertices.first()
  assertEquals("A", vertex.value)
 }

 @Test
 fun testTwoVerticesOneEdge() {
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addEdge("A", "B", "e1", IntRing.one)
  assertEquals(2, graph.vertices.size)
  val vA = graph.vertices.first { it.value == "A" }
  val vB = graph.vertices.first { it.value == "B" }
  assertTrue(vA.adjacencyList.contains(vB))
  assertFalse(vB.adjacencyList.contains(vA))
 }

 @Test
 fun testEdgeNonExistent() {
  assertThrows<NoSuchElementException> {
   graph.addEdge("X", "Y", "e", IntRing.one)
  }
  graph.addVertex("A")
  assertThrows<NoSuchElementException> {
   graph.addEdge("A", "Y", "e", IntRing.one)
  }
 }

 @Test
 fun testLoop() {
  graph.addVertex("A")
  graph.addEdge("A", "A", "loop", IntRing.one)
  assertEquals(1, graph.vertices.size)
  val vA = graph.vertices.first { it.value == "A" }
  assertTrue(vA.adjacencyList.contains(vA))
 }
}
