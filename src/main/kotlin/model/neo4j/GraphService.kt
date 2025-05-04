package model.neo4j

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.Graph
import model.DirectedGraph
import model.UndirectedGraph
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.loadAll
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring

object GraphService {
	var sessionFactory: SessionFactory? = null
	var uri = ""
	var user = ""
	var pass: String = ""
		get() = "secret"
		set(value) {
			field = value
			val configuration = Configuration.Builder()
				.uri(uri)
				.credentials(user, value)
				.build()
			sessionFactory = SessionFactory(configuration, "model.neo4j")
		}

	private val mapper: ObjectMapper = jacksonObjectMapper().activateDefaultTyping(
		LaissezFaireSubTypeValidator.instance,
		ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE
	)

	fun <T> toJson(obj: T): String = mapper.writeValueAsString(obj)

	fun <T> fromJson(json: String, type: Class<T>): T = mapper.readValue(json, type)

	fun <V : Any, K : Any, W : Comparable<W>> loadGraph(
		isDirected: Boolean
	): Graph<V, K, W> {
		val factory = sessionFactory ?: error("SessionFactory is not initialized")
		val session = factory.openSession()
		val edgeEntities = session.loadAll(EdgeEntity::class.java)
		val vertEntities = session.loadAll(VertexEntity::class.java)
		val ring: Ring<W> = if (edgeEntities.isEmpty()) IntRing as Ring<W>
		else determineRingType(edgeEntities)

		val graph = if (isDirected) DirectedGraph<V, K, W>(ring)
		else UndirectedGraph<V, K, W>(ring)

		vertEntities.forEach { ent ->
			val vClass = Class.forName(normalize(ent.dataType))
			val value = fromJson(ent.dataJson, vClass)
			graph.addVertex(value as V)
		}

		val idToVertex: Map<Long, model.Vertex<V>> = vertEntities.mapIndexed { index, ent ->
			ent.id!! to graph.vertices.elementAt(index)
		}.toMap()

		edgeEntities.forEach { ent ->
			val u = idToVertex[ent.start?.id!!]!!
			val v = idToVertex[ent.end?.id!!]!!
			val kClass = Class.forName(normalize(ent.keyType))
			val key = fromJson(ent.keyJson, kClass)
			val wClass = Class.forName(normalize(ent.weightType))
			val weight = fromJson(ent.weightJson, wClass)
			graph.addEdge(u.value, v.value, key as K, weight as W)
		}

		session.clear()
		return graph
	}

	fun <V, K, W : Comparable<W>> saveGraph(graph: Graph<V, K, W>) {
		val factory = sessionFactory ?: error("SessionFactory is not initialized")
		val session = factory.openSession()
		session.query("MATCH (n) DETACH DELETE n", emptyMap<String, Any>())

		val entityMap = graph.vertices.associateWith { vertex ->
			VertexEntity().apply {
				dataJson = toJson(vertex.value)
				dataType = vertex.value!!::class.java.name
			}
		}
		entityMap.values.forEach { session.save(it) }

		graph.edges.forEach { edgeModel ->
			val entity = EdgeEntity().apply {
				start = entityMap[edgeModel.startVertex]
				end = entityMap[edgeModel.endVertex]
				keyJson = toJson(edgeModel.key)
				keyType = edgeModel.key!!::class.java.name
				weightJson = toJson(edgeModel.weight)
				weightType = edgeModel.weight!!::class.java.name
			}
			session.save(entity)
		}

		session.clear()
	}

	private fun <W : Comparable<W>> determineRingType(
		entities: Collection<EdgeEntity>
	): Ring<W> {
		val typeName = entities.first().weightType
		@Suppress("UNCHECKED_CAST")
		return when (typeName) {
			Int::class.java.name, "int" -> IntRing
			java.lang.Long::class.java.name, "long" -> IntRing
			java.lang.Short::class.java.name, "short" -> IntRing
			java.lang.Byte::class.java.name, "byte" -> IntRing
			java.lang.Double::class.java.name, "double" -> Float64Field
			java.lang.Float::class.java.name, "float" -> Float64Field
			else -> error("Can't load this type of weight. Type: $typeName")
		} as Ring<W>
	}

	private fun normalize(typeName: String): String = when (typeName) {
		"int" -> Int::class.javaObjectType.name
		"long" -> java.lang.Long::class.java.name
		"short" -> java.lang.Short::class.java.name
		"byte" -> java.lang.Byte::class.java.name
		"double" -> java.lang.Double::class.java.name
		"float" -> java.lang.Float::class.java.name
		else -> typeName
	}
}
