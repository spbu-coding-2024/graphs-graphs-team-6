package viewmodel

import model.UndirectedGraph
import model.Edge
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import space.kscience.kmath.operations.IntRing

class MSFFinderTest {

 @Test
 fun testEmptyGraph() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  val (edgeLists, weights) = MSFFinder(graph).findMSFKruscal()
  assertTrue(edgeLists.isEmpty(), "Empty graph should produce no components")
  assertTrue(weights.isEmpty(), "Empty graph should produce no weights")
 }

 @Test
 fun testSingleVertex() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  val (edgeLists, weights) = MSFFinder(graph).findMSFKruscal()

  assertEquals(1, edgeLists.size, "Graph with one vertex should yield one component")
  assertEquals(1, weights.size, "Graph with one vertex should yield one weight entry")
  assertTrue(edgeLists[0].isEmpty(), "Single-vertex MST should have no edges")
  assertEquals(IntRing.zero, weights[0], "Single-vertex MST weight should be zero")
 }

 @Test
 fun testSimpleTree() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addVertex("C")
  graph.addEdge("A", "B", 1, 5)
  graph.addEdge("B", "C", 2, 3)

  val (edgeLists, weights) = MSFFinder(graph).findMSFKruscal()
  assertEquals(1, edgeLists.size, "Tree graph should yield one component")
  assertEquals(1, weights.size)

  val mst = edgeLists[0]
  assertEquals(2, mst.size, "Tree MST should contain two edges")
  val sortedWeights = mst.map { it.weight }.sorted()
  assertEquals(listOf(3, 5), sortedWeights, "MST edges should have weights 3 and 5")
  assertEquals(8, weights[0], "MST total weight should be 8")
 }

 @Test
 fun testCycleGraph() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addVertex("C")
  graph.addEdge("A", "B", 1, 1)
  graph.addEdge("B", "C", 2, 2)
  graph.addEdge("C", "A", 3, 3)

  val (edgeLists, weights) = MSFFinder(graph).findMSFKruscal()
  assertEquals(1, edgeLists.size, "Cycle graph should yield one component")
  assertEquals(1, weights.size)

  val mst = edgeLists[0]
  assertEquals(2, mst.size, "Cycle MST should contain two edges")
  val sortedWeights = mst.map { it.weight }.sorted()
  assertEquals(listOf(1, 2), sortedWeights, "MST should include edges of weight 1 and 2")
  assertEquals(3, weights[0], "MST total weight should be 3")
 }

 @Test
 fun testDisconnectedGraph() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addVertex("C")
  graph.addVertex("D")
  graph.addEdge("A", "B", 1, 4)
  graph.addEdge("C", "D", 2, 7)

  val (edgeLists, weights) = MSFFinder(graph).findMSFKruscal()
  assertEquals(2, edgeLists.size, "Disconnected graph should yield two components")
  assertEquals(2, weights.size)

  val firstMST = edgeLists[0]
  val secondMST = edgeLists[1]
  val sortedWeights = weights.sorted()
  assertEquals(listOf(4, 7), sortedWeights, "Weights should be 4 and 7")
  assertEquals(1, firstMST.size)
  assertEquals(1, secondMST.size)
 }

 @Test
 fun testMultipleComponentsWithLoopsAndParallelEdges() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing).apply {
   listOf("A","B","C","D","E","F","G","H").forEach { addVertex(it) }
   var idx = 0
   addEdge("A","B", idx++)
   addEdge("B","C", idx++)
   addEdge("C","A", idx++)
   addEdge("C","C", idx++)

   addEdge("D","E", idx++)
   addEdge("E","F", idx++)
   addEdge("F","D", idx++)

   addEdge("G","H", idx++)
   addEdge("H","G", idx++)
  }

  val (edgeLists, weights) = MSFFinder(graph).findMSFKruscal()
  val totalEdges = edgeLists.sumOf { it.size }
  assertEquals(5, totalEdges, "Should have 5 MST edges across all components")
  assertEquals(3, edgeLists.size, "Should detect three separate components")
  val componentSizes = edgeLists.map { it.size }.sorted()
  assertEquals(listOf(1,2,2), componentSizes, "Component MST sizes should be 2,2 and 1")
 }
}
