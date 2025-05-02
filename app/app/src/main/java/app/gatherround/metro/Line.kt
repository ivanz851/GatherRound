package app.gatherround.metro

import kotlinx.serialization.Serializable

/**
 * DTO для линия метро.
 * *Ключи-строки (`name`, `textStart`, `textEnd`) хранятся в виде словарей «язык → текст»,
 * что облегчает перевод.*
 *
 * @property id               уникальный идентификатор
 * @property perspective      (не используется)
 * @property name             локализованные названия линии (например, `"en" → "Circle Line"`)
 * @property color            цвет линии
 * @property icon             SVG-иконка линии
 * @property ordering         (не используется)
 * @property stationStartId   id стартовой станции линии
 * @property stationEndId     id финишной станции линии
 * @property textStart        подписи у начальной станции (язык → строка)
 * @property textEnd          подписи у конечной станции (язык → строка)
 * @property neighboringLines id линий, которые пересекающихся с данной
 */
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
