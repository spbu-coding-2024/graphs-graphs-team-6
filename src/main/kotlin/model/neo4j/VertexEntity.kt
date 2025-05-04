package model.neo4j

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity("Vertex")
open class VertexEntity @JvmOverloads constructor(
	@Id @GeneratedValue
	var id: Long? = null,
	var modelID: Long = 0,
	var dataJson: String = "",
	var dataType: String = ""
)
