package app.gatherround.metro

import kotlinx.serialization.Serializable

/** Код русского языка, используемый в словарях `name`, `title` и др. */
const val RUSSIAN = "ru"

/**
 * DTO для станции метро.
 * Объекты сравниваются по локализованному названию на русском (ключ `"ru"` в карте
 * [name]), а при совпадении — по id линии ([lineId]).
 *
 * @property id                 уникальный идентификатор станции
 * @property name               map локализованных названий станции, ключ — код языка (`"ru"` или `"en"`)
 * @property lineId             идентификатор линии метро, к которой принадлежит станция
 * @property location           географические координаты станции (широта/долгота) либо `null`, если неизвестны
 * @property exits              список выходов на поверхность; каждый выход описан объектом [Exit]
 * @property scheduleTrains     расписание поездов по направлениям: ключ — id станции-назначения, значение — список [TrainSchedule]
 * @property workTime           интервалы работы касс/вестибюлей станции (список [WorkTime])
 * @property services           (не используется)
 * @property enterTime          (не используется)
 * @property exitTime           (не используется)
 * @property ordering           (не используется)
 * @property mcd                `true`, если станция относится к МЦД; `null` — нет информации
 * @property outside            `true`, если станция открытого типа (платформа на улице); `null` — нет информации
 * @property mcc                `true`, если станция принадлежит МКЦ; `null` — нет информации
 * @property history            (не используется)
 * @property audios             (не используется)
 * @property accessibilityImages (не используется)
 * @property buildingImages     (не используется)
 * @property stationSvg         SVG-иконка станции
 * @property textSvg            SVG-текст с названием станции
 * @property tapSvg             активная зона ( hit-area ) для тапа/клика по станции
 */
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

/** DTO для географическая точка (широта, долгота) */
@Serializable
data class Location(val lat: Double, val lon: Double)

/** DTO для выхода из станции на поверхность */
@Serializable
data class Exit(
    val title: Map<String, String>,
    val exitNumber: Int,
    val location: Location?,
    val bus: String?,
    val trolleybus: String?,
    val tram: String?
)

/** DTO для расписание отправлений поездов */
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