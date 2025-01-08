package app.gatherround.metro

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

const val SECS_IN_MIN = 60
const val MINS_IN_HOUR = 60
const val SECS_IN_HOUR = SECS_IN_MIN * MINS_IN_HOUR
const val MAX_ROUTE_TIME = 5 * SECS_IN_HOUR

const val metroDataJsonPath = "C:\\\\Users\\\\test\\\\user\\\\ProjectSeminar2024-25\\\\GatherRound\\\\app\\\\app\\\\src\\\\main\\\\resources\\\\get-scheme-metadata.json"





val stationEngRusMap = mapOf(
    "Bulvar Rokossovskogo" to "Бульвар Рокоссовского",
    "Cherkizovskaya" to "Черкизовская",
    "Preobrazhenskaya Ploshchad" to "Преображенская площадь",
    "Sokolniki" to "Сокольники",
    "Krasnoselskaya" to "Красносельская",
    "Komsomolskaya" to "Комсомольская",
    "Krasniye Vorota" to "Красные ворота",
    "Chistiye Prudy" to "Чистые пруды",
    "Lubyanka" to "Лубянка",
    "Okhotny Ryad" to "Охотный ряд",
    "Biblioteka Imeni Lenina" to "Библиотека имени Ленина",
    "Kropotkinskaya" to "Кропоткинская",
    "Park Kultury" to "Парк культуры",
    "Frunzenskaya" to "Фрунзенская",
    "Sportivnaya" to "Спортивная",
    "Vorobyovy Gory" to "Воробьёвы горы",
    "Universitet" to "Университет",
    "Prospekt Vernadskogo" to "Проспект Вернадского",
    "Yugo-Zapadnaya" to "Юго-Западная",
    "Rechnoy Vokzal" to "Речной вокзал",
    "Vodny Stadion" to "Водный стадион",
    "Voykovskaya" to "Войковская",
    "Sokol" to "Сокол",
    "Aeroport" to "Аэропорт",
    "Dinamo" to "Динамо",
    "Belorusskaya" to "Белорусская",
    "Mayakovskaya" to "Маяковская",
    "Tverskaya" to "Тверская",
    "Teatralnaya" to "Театральная",
    "Novokuznetskaya" to "Новокузнецкая",
    "Paveletskaya" to "Павелецкая",
    "Avtozavodskaya" to "Автозаводская",
    "Kolomenskaya" to "Коломенская",
    "Kashirskaya" to "Каширская",
    "Kantemirovskaya" to "Кантемировская",
    "Tsaritsyno" to "Царицыно",
    "Orekhovo" to "Орехово",
    "Domodedovskaya" to "Домодедовская",
    "Krasnogvardeyskaya" to "Красногвардейская",
    "Shchyolkovskaya" to "Щёлковская",
    "Pervomayskaya" to "Первомайская",
    "Izmaylovskaya" to "Измайловская",
    "Partizanskaya" to "Партизанская",
    "Semyonovskaya" to "Семеновская",
    "Elektrozavodskaya" to "Электрозаводская",
    "Baumanskaya" to "Бауманская",
    "Kurskaya" to "Курская",
    "Ploshchad Revolyutsii" to "Площадь Революции",
    "Arbatskaya" to "Арбатская",
    "Smolenskaya" to "Смоленская",
    "Kiyevskaya" to "Киевская",
    "Park Pobedy" to "Парк Победы",
    "Slavyansky Bulvar" to "Славянский бульвар",
    "Kuntsevskaya" to "Кунцевская",
    "Molodyozhnaya" to "Молодёжная",
    "Krylatskoye" to "Крылатское",
    "Strogino" to "Строгино",
    "Pionerskaya" to "Пионерская",
    "Filyovsky Park" to "Филёвский парк",
    "Bagrationovskaya" to "Багратионовская",
    "Fili" to "Фили",
    "Kutuzovskaya" to "Кутузовская",
    "Studencheskaya" to "Студенческая",
    "Alexandrovsky Sad" to "Александровский сад",
    "Vystavochnaya" to "Выставочная",
    "Mezhdunarodnaya" to "Международная",
    "Oktyabrskaya" to "Октябрьская",
    "Dobryninskaya" to "Добрынинская",
    "Prospekt Mira" to "Проспект Мира",
    "Novoslobodskaya" to "Новослободская",
    "Krasnopresnenskaya" to "Краснопресненская",
    "Medvedkovo" to "Медведково",
    "Babushkinskaya" to "Бабушкинская",
    "Sviblovo" to "Свиблово",
    "Botanichesky Sad" to "Ботанический сад",
    "VDNKh" to "ВДНХ",
    "Alexeyevskaya" to "Алексеевская",
    "Rizhskaya" to "Рижская",
    "Sukharevskaya" to "Сухаревская",
    "Turgenevskaya" to "Тургеневская",
    "Kitay-Gorod" to "Китай-город",
    "Tretyakovskaya" to "Третьяковская",
    "Shabolovskaya" to "Шаболовская",
    "Leninsky Prospekt" to "Ленинский проспект",
    "Akademicheskaya" to "Академическая",
    "Profsoyuznaya" to "Профсоюзная",
    "Noviye Cheryomushki" to "Новые Черёмушки",
    "Kaluzhskaya" to "Калужская",
    "Belyayevo" to "Беляево",
    "Konkovo" to "Коньково",
    "Tyoplyi Stan" to "Тёплый стан",
    "Yasenevo" to "Ясенево",
    "Novoyasenevskaya" to "Новоясеневская"
)








@Serializable
data class MetroData(
    val lines: Map<String, MetroLine> = emptyMap(),
    val stations: Map<Int, MetroStation> = emptyMap(),
    val links: Map<String, Link> = emptyMap(),
    val transfers: Map<String, Transfer> = emptyMap()
) {
    private var stationNameIdMap: Map<Pair<String, Int>, Int> = emptyMap()

    init {
        stations.forEach { (key, station) ->
            station.stationUniqueId = key
        }

        stationNameIdMap = fillStationNameIdMap()
    }

    fun loadMetroDataFromFile(filePath: String = metroDataJsonPath): MetroData {
        val file = File(filePath)
        if (!file.exists()) {
            throw FileNotFoundException("File not found: $filePath")
        }

        val jsonContent = file.readText()
        val json = Json {
            ignoreUnknownKeys = true
        }

        return json.decodeFromString<MetroData>(jsonContent)
    }

    private fun fillStationNameIdMap(): Map<Pair<String, Int>, Int> {
        val result = mutableMapOf<Pair<String, Int>, Int>()

        for ((_, station) in stations) {
            val key = Pair(station.name, station.lineId)
            result[key] = station.stationUniqueId!!
        }

        return result
    }


    fun getStationById(stationId: Int): MetroStation? {
        return stations[stationId]
    }

    fun getStationByNameAndLineId(stationName: String, stationLineId: Int): MetroStation? {
        val stationId: Int? = stationNameIdMap[Pair(stationName, stationLineId)]
        return if (stationId == null) {
            null
        } else {
            stations[stationId]
        }
    }


    fun printStationNameIdMap() {
        stationNameIdMap.forEach { (key, value) ->
            val (stationName, lineId) = key
            println("Станция: $stationName, Линия: $lineId, ID: $value")
        }
    }



    fun printStations() {
        stations.forEach { (id, station) ->
            println("ID: $id, Станция: ${station.name}, Линия: ${station.lineId}, Label ID: ${station.labelId}, Переходная: ${station.isTransferStation}")
        }
    }

}
