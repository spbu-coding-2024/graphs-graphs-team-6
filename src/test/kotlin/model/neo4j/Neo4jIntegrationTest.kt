import org.junit.jupiter.api.*
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import model.neo4j.GraphService
import model.graph.DirectedGraph
import space.kscience.kmath.operations.IntRing
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Neo4jIntegrationTest {
	private lateinit var embeddedNeo4j: Neo4j

	@BeforeAll
	fun startNeo4j() {
		embeddedNeo4j = Neo4jBuilders.newInProcessBuilder()
			.withDisabledServer()
			.build()
		GraphService.uri = embeddedNeo4j.boltURI().toString()
		GraphService.user = "neo4j"
		GraphService.pass = ""
	}

	@AfterAll
	fun stopNeo4j() {
		embeddedNeo4j.close()
	}

	/**
	 * Integration test for GraphService with embedded Neo4j: Typical usage of Neo4j in this application
	 *
	 * - Starts an in‑process Neo4j instance before all tests.
	 * - Saves and loads a simple directed graph (vertices X, Y; edge X->Y with weight 10).
	 * - Verifies that vertices, edge count, and weight match.
	 * - Shuts down Neo4j after all tests.
	 */
	@Test
	fun `save and load directed graph`() {
		val graph = DirectedGraph<String, Int, Int>(IntRing).apply {
			addVertex("X"); addVertex("Y")
			addEdge("X", "Y", 1, 10)
		}

		GraphService.saveGraph(graph)
		val loaded = GraphService.loadGraph<String, String, Int>(isDirected = true)

		assertEquals(setOf("X", "Y"), loaded.vertices.map { it.value }.toSet())
		assertEquals(1, loaded.edges.size)
		assertEquals(10, loaded.edges.first().weight)
	}
}
