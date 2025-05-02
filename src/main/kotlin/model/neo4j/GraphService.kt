package model.neo4j

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import model.Vertex
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import space.kscience.kmath.operations.Ring

class GraphService(uri: String = "bolt://localhost:7687", user: String = "neo4j", pass: String = "secret") { // TODO
	var sessionFactory: SessionFactory

	val mapper = jacksonObjectMapper().activateDefaultTyping(
		LaissezFaireSubTypeValidator.instance,
		ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE
	)

	fun <T> toJson(obj: T): String = mapper.writeValueAsString(obj)

	fun <T> fromJson(json: String, type: Class<T>): T = mapper.readValue(json, type)

	init {
		val configuration = Configuration.Builder()
			.uri(uri)
			.credentials(user, pass)
			.build()

		sessionFactory = SessionFactory(configuration, "")
	}

	fun <V : Any, K : Any, W : Comparable<W>> loadGraph(
		ring: Ring<W>,
		isDirected: Boolean
	): Graph<V, K, W> {
		val session = sessionFactory.openSession()
		val graph = if (isDirected) DirectedGraph<V, K, W>(ring) else UndirectedGraph<V, K, W>(ring)

		val vertEntities = session.loadAll(VertexEntity::class.java)
		val idToValue: Map<Long, V> = vertEntities.associate { ent ->
			val vClass = Class.forName(ent.dataType)
			val value = fromJson(ent.dataJson, vClass)
			ent.id!! to value
		} as Map<Long, V>
		idToValue.values.forEach { graph.addVertex(it) }

		val idToVertex: Map<Long, model.Vertex<V>> = idToValue.mapValues { (_, value) ->
			graph.vertices.first { it.value == value }
		}

		val edgeEntities = session.loadAll(EdgeEntity::class.java)
		edgeEntities.forEach { ent ->
			val u = idToVertex[ent.start?.id!!]!!
			val v = idToVertex[ent.end?.id!!]!!
			val kClass = Class.forName(ent.keyType)
			val key = fromJson(ent.keyJson, kClass)

			val wClass = Class.forName(ent.weightType)
			val weight = fromJson(ent.weightJson, wClass)
			graph.addEdge(u.value, v.value, key as K, weight as W)
		}

		return graph
	}

	fun <V, K, W : Comparable<W>> saveGraph(graph: Graph<V, K, W>) {
		val session = sessionFactory.openSession()

		val entityMap = graph.vertices.associateWith {
			val entity = VertexEntity()
			entity.dataJson = toJson(it.value)
			entity.dataType = it.value!!::class.qualifiedName ?: ""
			entity
		}

		entityMap.values.forEach {
			session.save(it)
		}

		graph.edges.forEach {
			val entity = EdgeEntity()
			entity.start = entityMap[it.startVertex]
			entity.end = entityMap[it.endVertex]
			entity.keyJson = toJson(it.key)
			entity.keyType = it.key!!::class.qualifiedName ?: ""
			entity.weightJson = toJson(it.weight)
			entity.weightType = it.weight::class.qualifiedName ?: ""
			session.save(entity)
		}
	}
}