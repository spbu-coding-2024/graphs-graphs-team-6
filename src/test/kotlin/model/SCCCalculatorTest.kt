package model

import androidx.compose.ui.graphics.Color
import model.utils.SCCCalculator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing

class SCCCalculatorTest {

 @Test
 fun `single cycle`() {
  val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
   addVertex("A")
   addVertex("B")
   addVertex("C")

   // теперь нужно передавать weight как элемент кольца (IntRing.one == 1)
   addEdge("A", "B", 1, IntRing.one)
   addEdge("B", "C", 2, IntRing.one)
   addEdge("C", "A", 3, IntRing.one)
  }

  val calculator = SCCCalculator<String, Int, Int>()
  lateinit var colors: Map<Vertex<String>, Color>
  calculator.onComputeListener = { computed -> colors = computed }
  calculator.calculateComponents(graph)

  val groups = colors.entries
   .groupBy({ it.value }, { it.key.element })
   .values
   .map { it.toSet() }
   .toSet()

     Assertions.assertEquals(setOf(setOf("A", "B", "C")), groups)
 }

 @Test
 fun `cycle with tail`() {
  val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
   addVertex("A"); addVertex("B"); addVertex("C"); addVertex("D")

   addEdge("A", "B", 1, IntRing.one)
   addEdge("B", "C", 2, IntRing.one)
   addEdge("C", "A", 3, IntRing.one)
   addEdge("C", "D", 4, IntRing.one)
  }

  val calculator = SCCCalculator<String, Int, Int>()
  lateinit var colors: Map<Vertex<String>, Color>
  calculator.onComputeListener = { computed -> colors = computed }
  calculator.calculateComponents(graph)

  val groups = colors.entries
   .groupBy({ it.value }, { it.key.element })
   .values
   .map { it.toSet() }
   .toSet()

     Assertions.assertEquals(setOf(setOf("A", "B", "C"), setOf("D")), groups)
 }

 @Test
 fun `multiple components`() {
  val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
   addVertex("A"); addVertex("B"); addVertex("C")
   addVertex("D"); addVertex("E"); addVertex("F")

   addEdge("A", "B", 1, IntRing.one)
   addEdge("B", "A", 2, IntRing.one)
   addEdge("C", "C", 3, IntRing.one)
   addEdge("D", "E", 4, IntRing.one)
   addEdge("E", "F", 5, IntRing.one)
   addEdge("F", "D", 6, IntRing.one)
  }

  val calculator = SCCCalculator<String, Int, Int>()
  lateinit var colors: Map<Vertex<String>, Color>
  calculator.onComputeListener = { computed -> colors = computed }
  calculator.calculateComponents(graph)

  val groups = colors.entries
   .groupBy({ it.value }, { it.key.element })
   .values
   .map { it.toSet() }
   .toSet()

     Assertions.assertEquals(
         setOf(
             setOf("A", "B"),
             setOf("C"),
             setOf("D", "E", "F")
         ),
         groups
     )
 }
}