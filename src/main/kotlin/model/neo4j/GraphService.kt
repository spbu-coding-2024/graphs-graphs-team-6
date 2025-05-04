package model.neo4j

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import model.*
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.session.SessionFactory
import org.neo4j.ogm.session.loadAll
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer

object GraphService { // TODO
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

	val mapper = jacksonObjectMapper().activateDefaultTyping(
		LaissezFaireSubTypeValidator.instance,
		ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE
	)

	fun <T> toJson(obj: T): String = mapper.writeValueAsString(obj)

	fun <T> fromJson(json: String, type: Class<T>): T = mapper.readValue(json, type)

	fun <V : Any, K : Any, W : Comparable<W>> loadGraph(
		isDirected: Boolean
	): Graph<V, K, W> {
		val factory = sessionFactory
		if (factory == null) error("SessionFactory is not initialized")
		else {
			val session = factory.openSession()
			val edgeEntities = session.loadAll(EdgeEntity::class.java)
			val vertEntities = session.loadAll(VertexEntity::class.java).sortedBy { it.modelID }
			val ring: Ring<W> = if (edgeEntities.isEmpty()) IntRing as Ring<W>
			else determineRingType<W>(edgeEntities)

			val graph = if (isDirected) DirectedGraph<V, K, W>(ring) else UndirectedGraph<V, K, W>(ring)

			val idToModel: Map<Long, Pair<Long, V>> = vertEntities.associate { ent ->
				val vClass = Class.forName(ent.dataType)
				val value = fromJson(ent.dataJson, vClass)
				ent.id!! to (ent.modelID to value )
			} as Map<Long, Pair<Long, V>>
			idToModel.values.forEach { graph.addVertex(it.second) }

			val idToVertex: Map<Long, model.Vertex<V>> = idToModel.mapValues { (_, value) ->
				graph.vertices.first { it.id == value.first }
			}

			edgeEntities.forEach { ent ->
				val u = idToVertex[ent.start?.id!!]!!
				val v = idToVertex[ent.end?.id!!]!!
				val kClass = Class.forName(ent.keyType)
				val key = fromJson(ent.keyJson, kClass)

				val wClass = Class.forName(ent.weightType)
				val weight = fromJson(ent.weightJson, wClass)
				graph.addEdge(u.value, v.value, key as K, weight as W)

			}
			session.clear()
			return graph
		}
	}

	fun <V, K, W : Comparable<W>> saveGraph(graph: Graph<V, K, W>) {
		val factory = sessionFactory
		if (factory == null) error("SessionFactory is not initialized")
		else {
			val session = factory.openSession()
			session.query("MATCH (n) DETACH DELETE n", emptyMap<String, Any>())
			val entityMap = graph.vertices.associateWith {
				val entity = VertexEntity()
				entity.modelID = it.id
				entity.dataJson = toJson(it.value)
				entity.dataType = it.value!!::class.java.name ?: ""
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
				entity.keyType = it.key!!::class.java.name ?: ""
				entity.weightJson = toJson(it.weight)
				entity.weightType = it.weight::class.java.name ?: ""
				session.save(entity)
			}
			session.clear()
		}
	}

	private fun <W : Comparable<W>> determineRingType(entities: Collection<EdgeEntity>): Ring<W> {
		val typeName = entities.first().weightType
		@Suppress("UNCHECKED_CAST")
		return when (typeName) {
			Integer::class.java.name,
			java.lang.Byte::class.java.name,
			java.lang.Short::class.java.name,
			java.lang.Long::class.java.name -> IntRing

			java.lang.Double::class.java.name,
			java.lang.Float::class.java.name -> Float64Field

			else -> error("Can't load this type of weight. Type: $typeName")
		} as Ring<W>
	}

}
