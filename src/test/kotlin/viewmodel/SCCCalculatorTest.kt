package viewmodel

import model.DirectedGraph
import model.Vertex
import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SCCCalculatorTest {

 @Test
 fun `single cycle`() {
  val graph = DirectedGraph<String, Int>().apply {
   addVertex("A"); addVertex("B"); addVertex("C")
   addEdge("A", "B", 1)
   addEdge("B", "C", 2)
   addEdge("C", "A", 3)
  }
  val calculator = SCCCalculator<String, Int>()
  lateinit var colors: Map<Vertex<String>, Color>
  calculator.onComputeListener = { computed -> colors = computed }
  calculator.calculateComponents(graph)
  val groups = colors.entries
   .groupBy({ it.value }, { it.key.element })
   .values
   .map { it.toSet() }
   .toSet()
  assertEquals(setOf(setOf("A", "B", "C")), groups)
 }

 @Test
 fun `cycle with tail`() {
  val graph = DirectedGraph<String, Int>().apply {
   addVertex("A"); addVertex("B"); addVertex("C"); addVertex("D")
   addEdge("A", "B", 1)
   addEdge("B", "C", 2)
   addEdge("C", "A", 3)
   addEdge("C", "D", 4)
  }
  val calculator = SCCCalculator<String, Int>()
  lateinit var colors: Map<Vertex<String>, Color>
  calculator.onComputeListener = { computed -> colors = computed }
  calculator.calculateComponents(graph)
  val groups = colors.entries
   .groupBy({ it.value }, { it.key.element })
   .values
   .map { it.toSet() }
   .toSet()
  assertEquals(setOf(setOf("A", "B", "C"), setOf("D")), groups)
 }

 @Test
 fun `multiple components`() {
  val graph = DirectedGraph<String, Int>().apply {
   addVertex("A"); addVertex("B"); addVertex("C")
   addVertex("D"); addVertex("E"); addVertex("F")
   addEdge("A", "B", 1)
   addEdge("B", "A", 2)
   addEdge("C", "C", 3)
   addEdge("D", "E", 4)
   addEdge("E", "F", 5)
   addEdge("F", "D", 6)
  }
  val calculator = SCCCalculator<String, Int>()
  lateinit var colors: Map<Vertex<String>, Color>
  calculator.onComputeListener = { computed -> colors = computed }
  calculator.calculateComponents(graph)
  val groups = colors.entries
   .groupBy({ it.value }, { it.key.element })
   .values
   .map { it.toSet() }
   .toSet()
  assertEquals(setOf(setOf("A", "B"), setOf("C"), setOf("D", "E", "F")), groups)
 }
}
