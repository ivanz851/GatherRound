package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class Transition(
    val id: Int,
    val perspective: Boolean,
    val stationFromId: Int,
    val stationToId: Int,
    val pathLength: Int,
    val videoFrom: String?,
    val videoTo: String?,
    val bi: Boolean,
    val ground: Boolean,
    val svg: String,
    val wagons: List<WagonInfo>
)

@Serializable
data class WagonInfo(
    val stationToId: Int,
    val stationPrevId: Int,
    val types: List<String>
)
