package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class MetroLine(
    val name: String,
    val color: String
)