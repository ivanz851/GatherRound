package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class MetroStation(
    val name: String,
    val lineId: Int,
    val labelId: Int,
    val boardInfo: BoardInfo? = null,
    val linkIds: List<Int>,
    val isTransferStation: Boolean = false
)
