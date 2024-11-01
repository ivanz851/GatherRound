package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class BoardInfo(
    val exit: List<ExitPosition>,
    val transfer: List<TransferPosition> = emptyList()
)
