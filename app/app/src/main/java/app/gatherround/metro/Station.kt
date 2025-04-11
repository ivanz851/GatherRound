package app.gatherround.metro

import kotlinx.serialization.Serializable

const val RUSSIAN = "ru"

@Serializable
data class Station(
    var id: Int = 0,
    val name: Map<String, String> = emptyMap(),
    val lineId: Int = 0,
    val location: Location? = null,
    val exits: List<Exit> = emptyList(),
    val scheduleTrains: Map<String, List<TrainSchedule>> = emptyMap(),
    val workTime: List<WorkTime> = emptyList(),
    val services: List<String> = emptyList(),
    val enterTime: Int? = null,
    val exitTime: Int? = null,
    val ordering: Int = 0,
    val mcd: Boolean? = null,
    val outside: Boolean? = null,
    val mcc: Boolean? = null,
    val history: String? = null,
    val audios: List<String> = emptyList(),
    val accessibilityImages: List<String> = emptyList(),
    val buildingImages: List<String> = emptyList(),
    val stationSvg: SvgData? = null,
    val textSvg: SvgText? = null,
    val tapSvg: SvgTap? = null,
) : Comparable<Station> {
    override fun compareTo(other: Station): Int {
        val thisNameEn = this.name[RUSSIAN]!!
        val otherNameEn = other.name[RUSSIAN]!!

        val nameComparison = thisNameEn.compareTo(otherNameEn)
        if (nameComparison != 0) return nameComparison

        return this.lineId.compareTo(other.lineId)
    }

    override fun toString(): String {
        return "Station(name=$name, lineId=$lineId, id=$id)"
    }
}

@Serializable
data class Location(val lat: Double, val lon: Double)

@Serializable
data class Exit(
    val title: Map<String, String>,
    val exitNumber: Int,
    val location: Location?,
    val bus: String?,
    val trolleybus: String?,
    val tram: String?
)

@Serializable
data class TrainSchedule(
    val stationToId: Int,
    val stationToName: String,
    val first: String,
    val last: String?,
    val dayType: String,
    val weekend: Boolean
)

@Serializable
data class WorkTime(val open: String?, val close: String?)

@Serializable
data class SvgData(val svg: String, val x: Double, val y: Double)

@Serializable
data class SvgText(val svg: String, val x: Double, val y: Double, val h: Double, val w: Double)

@Serializable
data class SvgTap(val x: Double, val y: Double, val h: Double, val w: Double)