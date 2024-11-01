package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class Transfer(
    val stationIds: List<Int>
)
