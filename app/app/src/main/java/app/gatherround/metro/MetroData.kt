package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class MetroData(
    val lines: Map<String, MetroLine>,
    val stations: Map<String, MetroStation>,
    val links: Map<String, Link>,
    val transfers: Map<String, Transfer>
)
