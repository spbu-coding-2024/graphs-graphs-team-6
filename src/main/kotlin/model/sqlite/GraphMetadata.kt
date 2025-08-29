package model.sqlite

data class GraphMetadata(
    val name: String,
    val typeV: String,
    val typeK: String,
    val typeW: String,
    val directed: Boolean
)

data class EdgeRow(
    val key: String,
    val start: String,
    val end: String,
    val weight: String
)
