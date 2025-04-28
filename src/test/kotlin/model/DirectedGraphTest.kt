package model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.IntRing

class DirectedGraphTest {
 // Теперь у нас три типа: V = String, K = String, W = Int
 private lateinit var graph: DirectedGraph<String, String, Int>

 @BeforeEach
 fun setup() {
  // Передаём графу кольцо целых чисел
  graph = DirectedGraph(IntRing)
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
  // При добавлении ребра указываем вес из кольца: IntRing.one == 1
  graph.addEdge("A", "B", "e1", IntRing.one)
  assertEquals(2, graph.vertices.size)
  val vA = graph.vertices.first { it.element == "A" }
  val vB = graph.vertices.first { it.element == "B" }
  assertTrue(vA.adjacencyList.contains(vB))
  assertFalse(vB.adjacencyList.contains(vA))
 }

 @Test
 fun testEdgeNonExistent() {
  // Оба конца не существуют
  assertThrows<NoSuchElementException> {
   graph.addEdge("X", "Y", "e", IntRing.one)
  }
  graph.addVertex("A")
  // Второй конец отсутствует
  assertThrows<NoSuchElementException> {
   graph.addEdge("A", "Y", "e", IntRing.one)
  }
 }

 @Test
 fun testLoop() {
  graph.addVertex("A")
  // Для петли тоже указываем вес
  graph.addEdge("A", "A", "loop", IntRing.one)
  assertEquals(1, graph.vertices.size)
  val vA = graph.vertices.first { it.element == "A" }
  assertTrue(vA.adjacencyList.contains(vA))
 }
}
