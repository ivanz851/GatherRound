package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class Line(
    val id: Int,
    val perspective: Boolean,
    val name: Map<String, String>,
    val color: String,
    val icon: String,
    val ordering: Int,
    val stationStartId: Int,
    val stationEndId: Int,
    val textStart: Map<String, String?>,
    val textEnd: Map<String, String?>,
    val neighboringLines: List<Int>
)
