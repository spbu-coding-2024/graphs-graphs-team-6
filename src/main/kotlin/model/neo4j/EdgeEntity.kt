package model.neo4j

import org.neo4j.ogm.annotation.*

@RelationshipEntity(type = "ADJACENT TO")
open class EdgeEntity @JvmOverloads constructor(
	@Id @GeneratedValue
	val id: Long? = null,

	@StartNode
	var start: VertexEntity? = null,

	@EndNode
	var end: VertexEntity? = null,

	var keyJson: String = "",
	var keyType: String = "",
	var weightJson: String = "",
	var weightType: String = "",
)