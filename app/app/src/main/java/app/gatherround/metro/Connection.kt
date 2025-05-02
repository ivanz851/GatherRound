package app.gatherround.metro

import kotlinx.serialization.Serializable

/**
 * DTO для перегона между двумя станциями в метро.
 *
 * @property id              уникальный идентификатор
 * @property perspective     (не используется)
 * @property stationFromId   id начальной станции
 * @property stationToId     id конечной станции
 * @property pathLength      время в секундах за которое состав преодолевает перегон
 * @property bi              (не используется)
 * @property svg             SVG-иконка линии
 * @property closedBackward  (не используется)
 */
@Serializable
data class Connection(
    val id: Int,
    val perspective: Boolean,
    val stationFromId: Int,
    val stationToId: Int,
    val pathLength: Int,
    val bi: Boolean,
    val svg: String,
    val closedBackward: Boolean?
)
