package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class TransferPosition(
    val toSt: Int,
    val nextSt: Int? = null,
    val prevSt: Int? = null,
    val pos: List<Int>
)
