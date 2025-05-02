package app.gatherround.metro

import kotlinx.serialization.Serializable

/**
 * DTO для перехода между двумя станциями.
 *
 * @property id            уникальный идентификатор
 * @property perspective   (не используется)
 * @property stationFromId id стартовой станции перехода
 * @property stationToId   id финишной станции перехода
 * @property pathLength    длительность пересадки в секундах
 * @property videoFrom     (не используется)
 * @property videoTo       (не используется)
 * @property bi            (не используется)
 * @property ground        (не используется)
 * @property svg           SVG-окинка перехода
 * @property wagons        (не используется)
 */
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

/**
 * DTO для информации об оптимальных вагонах для совершения пересадки.
 */
@Serializable
data class WagonInfo(
    val stationToId: Int,
    val stationPrevId: Int,
    val types: List<String>
)
