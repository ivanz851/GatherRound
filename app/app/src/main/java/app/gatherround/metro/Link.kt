package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val type: String,
    val fromStationId: Int,
    val toStationId: Int,
    val weightTime: Int,
    val weightTransfer: Int,
    val transferId: Long? = null
)
