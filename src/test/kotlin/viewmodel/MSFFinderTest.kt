package viewmodel

import model.UndirectedGraph
import model.UndirectedGraph.UndirectedEdge
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import space.kscience.kmath.operations.IntRing

/**
 * Unit tests for MSFFinder, verifying minimal spanning forest computation
 */
class MSFFinderTest {

 @Test
 fun testEmptyGraph() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  val result = MSFFinder(graph).findMSFKruscal()
  assertTrue(result.isEmpty(), "Пустой граф должен давать пустой лес")
 }

 @Test
 fun testSingleVertex() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  val result = MSFFinder(graph).findMSFKruscal()

  assertEquals(1, result.size, "Граф с одной вершиной должен дать один компонент")
  val (mst, totalWeight) = result[0]
  assertTrue(mst.isEmpty(), "В МНФ одиночной вершины нет рёбер")
  assertEquals(IntRing.zero, totalWeight, "Вес МНФ одиночной вершины должен быть ring.zero")
 }

 @Test
 fun testSimpleTree() {
  val graph = UndirectedGraph<String, Int, Int>(IntRing)
  graph.addVertex("A")
  graph.addVertex("B")
  graph.addVertex("C")
  // ключ и вес совпадают для простоты теста
  graph.addEdge("A", "B", 5, 5)
  graph.addEdge("B", "C", 3, 3)

  val result = MSFFinder(graph).findMSFKruscal()

  assertEquals(1, result.size, "Дерево из трёх вершин должно быть одним компонентом")
  val (mst, totalWeight) = result[0]
  assertEquals(2, mst.size, "В МНФ дерева из трёх вершин должно быть 2 ребра")
  assertTrue(mst.any { it.weight == 5 }, "Должно присутствовать ребро весом 5")
  assertTrue(mst.any { it.weight == 3 }, "Должно присутствовать ребро весом 3")
  assertEquals(8, totalWeight, "Суммарный вес МНФ должен быть 5 + 3 = 8")
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

  assertEquals(1, result.size, "Цикл из трёх вершин даёт один компонент")
  val (mst, totalWeight) = result[0]
  assertEquals(2, mst.size, "В МНФ цикла должно быть 2 ребра")
  assertTrue(mst.any { it.weight == 1 }, "Должно быть ребро весом 1")
  assertTrue(mst.any { it.weight == 2 }, "Должно быть ребро весом 2")
  assertFalse(mst.any { it.weight == 3 }, "Не должно быть ребра весом 3")
  assertEquals(3, totalWeight, "Суммарный вес МНФ должен быть 1 + 2 = 3")
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

  assertEquals(2, result.size, "Должно быть два компонента")
  // Сортируем компоненты по суммарному весу для предсказуемости
  val sorted = result.sortedBy { it.second }
  val (mst1, weight1) = sorted[0]
  val (mst2, weight2) = sorted[1]

  // Первый компонент вес 7 -> ребро весом 7
  assertEquals(1, mst1.size)
  assertEquals(4, mst1[0].weight)
  assertEquals(4, weight1)

  // Второй компонент вес 7
  assertEquals(1, mst2.size)
  assertEquals(7, mst2[0].weight)
  assertEquals(7, weight2)
 }
}
