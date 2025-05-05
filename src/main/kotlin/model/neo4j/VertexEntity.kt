package model.neo4j

import org.neo4j.ogm.annotation.GeneratedValue
import org.neo4j.ogm.annotation.Id
import org.neo4j.ogm.annotation.NodeEntity
import org.neo4j.ogm.annotation.Relationship

@NodeEntity("Vertex")
open class VertexEntity @JvmOverloads constructor(
	@Id @GeneratedValue
	val id: Long? = null,
	var dataJson: String = "",
	var dataType: String = ""
) {
	@Relationship(type = "ADJACENT TO", direction = Relationship.Direction.OUTGOING)
	val adjacencyList: MutableList<VertexEntity> = mutableListOf()
}
