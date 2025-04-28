package viewmodel

import model.UndirectedGraph
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import space.kscience.kmath.operations.IntRing

class MSFFinderTest {

 @Test
 fun testEmptyGraph() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  val result = MSFFinder(graph).findMSFKruscal()
  assertTrue(result.isEmpty(), "Empty graph should produce an empty forest")
 }

 @Test
 fun testSingleVertex() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  val result = MSFFinder(graph).findMSFKruscal()

  assertEquals(1, result.size, "Graph with one vertex should yield one component")
  val (mst, totalWeight) = result[0]
  assertTrue(mst.isEmpty(), "Single vertex MST should have no edges")
  assertEquals(IntRing.zero, totalWeight, "Single vertex MST weight should be zero")
 }

 @Test
 fun testSimpleTree() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addVertex("C")
  graph.addEdge("A", "B", 5, 5)
  graph.addEdge("B", "C", 3, 3)

  val result = MSFFinder(graph).findMSFKruscal()

  assertEquals(1, result.size, "Tree graph should yield one component")
  val (mst, totalWeight) = result[0]
  assertEquals(2, mst.size, "Tree MST should contain two edges")
  assertTrue(mst.any { it.weight == 5 }, "MST should include edge of weight 5")
  assertTrue(mst.any { it.weight == 3 }, "MST should include edge of weight 3")
  assertEquals(8, totalWeight, "MST total weight should be 8")
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

  val result = MSFFinder(graph).findMSFKruscal()

  assertEquals(1, result.size, "Cycle graph should yield one component")
  val (mst, totalWeight) = result[0]
  assertEquals(2, mst.size, "Cycle MST should contain two edges")
  assertTrue(mst.any { it.weight == 1 }, "MST should include edge of weight 1")
  assertTrue(mst.any { it.weight == 2 }, "MST should include edge of weight 2")
  assertFalse(mst.any { it.weight == 3 }, "MST should not include edge of weight 3")
  assertEquals(3, totalWeight, "MST total weight should be 3")
 }

 @Test
 fun testDisconnectedGraph() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addVertex("C")
  graph.addVertex("D")
  graph.addEdge("A", "B", 4, 4)
  graph.addEdge("C", "D", 7, 7)

  val result = MSFFinder(graph).findMSFKruscal()

  assertEquals(2, result.size, "Disconnected graph should yield two components")
  val sorted = result.sortedBy { it.second }
  val (mst1, weight1) = sorted[0]
  val (mst2, weight2) = sorted[1]

  assertEquals(1, mst1.size, "Each component MST should contain one edge")
  assertEquals(4, mst1[0].weight, "First component edge weight should be 4")
  assertEquals(4, weight1, "First component total weight should be 4")

  assertEquals(1, mst2.size, "Each component MST should contain one edge")
  assertEquals(7, mst2[0].weight, "Second component edge weight should be 7")
  assertEquals(7, weight2, "Second component total weight should be 7")
 }
}
