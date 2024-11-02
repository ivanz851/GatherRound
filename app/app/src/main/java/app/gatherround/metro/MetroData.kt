package app.gatherround.metro

import kotlinx.serialization.Serializable

@Serializable
data class MetroData(
    val lines: Map<String, MetroLine>,
    val stations: Map<Int, MetroStation>,
    val links: Map<String, Link>,
    val transfers: Map<String, Transfer>
) {
    private val stationNameIdMap: Map<String, Int> = fillStationNameIdMap()

    private fun fillStationNameIdMap(): Map<String, Int> {
        return stations.values.associate { it.name to it.labelId }
    }

    fun getStationById(stationId: Int): MetroStation? {
        return stations[stationId]
    }

    fun getStationByName(stationName: String): MetroStation? {
        val stationId: Int? = stationNameIdMap[stationName]
        return if (stationId == null) {
            null
        } else {
            stations[stationId]
        }
    }
}
