package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class ExitPosition(
    val pos: List<Int>
)
