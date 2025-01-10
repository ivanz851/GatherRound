package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val id: Int,
    val perspective: Boolean,
    val stationFromId: Int,
    val stationToId: Int,
    val pathLength: Int,
    val bi: Boolean,
    val svg: String,
    val closedBackward: Boolean?
)
