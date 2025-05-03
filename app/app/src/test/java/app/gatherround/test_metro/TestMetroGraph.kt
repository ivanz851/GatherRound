package app.gatherround.test_metro

import app.gatherround.graph.Dijkstra
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.RUSSIAN
import app.gatherround.metro.Station
import app.gatherround.metro.SECS_IN_MIN
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.abs

class TestMetroGraph {
    private val metroData = MetroData().loadMetroDataFromFile()
    private lateinit var metroGraph: MetroGraph

    @BeforeEach
    fun setUp() {
        metroGraph = MetroGraph(metroData)
    }

    @Test
    fun testDijkstraAlgorithmReachesStaionsAtSameLine() {
        val startStation =
            metroData.getStationByNameAndLineId("Третьяковская", 5)
        val finishStation =
            metroData.getStationByNameAndLineId("Медведково", 5)


        val dijkstra = Dijkstra<Station>()
        val (distances, ancestors) =
            dijkstra.calcShortestPathsFromVertex(metroGraph, startStation!!)

        assertTrue(distances[finishStation] != Int.MAX_VALUE,
            "Станция ${finishStation!!.name[RUSSIAN]} недостижима.")
    }

    @Test
    fun testDijkstraAlgorithmReachesAllStations() {
        val startStation =
            metroData.getStationByNameAndLineId("Третьяковская", 5)

        val dijkstra = Dijkstra<Station>()
        val (distances, ancestors) =
            dijkstra.calcShortestPathsFromVertex(metroGraph, startStation!!)

        for ((station, distance) in distances) {
            assertTrue(distance != Int.MAX_VALUE,
                "Станция ${station.name[RUSSIAN]} недостижима.")
        }

        for ((station, ancestor) in ancestors) {
            if (station != startStation) {
                assertNotNull(ancestor, "У станции ${station.name} нет предка.")
            }
        }
    }

    @Test
    fun testFindShortestPath_from_ParkPobedy3_to_Kotelniki8() {
        val expectedPath = listOf(
            metroData.getStationByNameAndLineId("Парк Победы", 3),
            metroData.getStationByNameAndLineId("Киевская", 3),

            metroData.getStationByNameAndLineId("Киевская", 6),
            metroData.getStationByNameAndLineId("Парк культуры", 6),
            metroData.getStationByNameAndLineId("Октябрьская", 6),
            metroData.getStationByNameAndLineId("Добрынинская", 6),
            metroData.getStationByNameAndLineId("Павелецкая", 6),
            metroData.getStationByNameAndLineId("Таганская", 6),

            metroData.getStationByNameAndLineId("Таганская", 8),
            metroData.getStationByNameAndLineId("Пролетарская", 8),
            metroData.getStationByNameAndLineId("Волгоградский проспект", 8),
            metroData.getStationByNameAndLineId("Текстильщики", 8),
            metroData.getStationByNameAndLineId("Кузьминки", 8),
            metroData.getStationByNameAndLineId("Рязанский проспект ", 8),
            metroData.getStationByNameAndLineId("Выхино", 8),
            metroData.getStationByNameAndLineId("Лермонтовский проспект", 8),
            metroData.getStationByNameAndLineId("Жулебино", 8),
            metroData.getStationByNameAndLineId("Котельники", 8),
        ).mapNotNull { it }

        val expectedTimeInSecs = 47 * SECS_IN_MIN

        verifyShortestPath(
            startStationName = "Парк Победы",
            startStationLineId = 3,
            finishStationName = "Котельники",
            finishStationLineId = 8,
            expectedPath = expectedPath,
            expectedTimeInSecs = expectedTimeInSecs,
        )
    }

    @Test
    fun testFindShortestPath_from_BitsevskyPark12_to_Kurskaya3() {
        val expectedPath = listOf(
            metroData.getStationByNameAndLineId("Битцевский парк", 12),

            metroData.getStationByNameAndLineId("Новоясеневская", 5),
            metroData.getStationByNameAndLineId("Ясенево", 5),
            metroData.getStationByNameAndLineId("Тёплый Стан", 5),
            metroData.getStationByNameAndLineId("Коньково", 5),
            metroData.getStationByNameAndLineId("Беляево", 5),
            metroData.getStationByNameAndLineId("Калужская", 5),
            metroData.getStationByNameAndLineId("Новые Черёмушки", 5),
            metroData.getStationByNameAndLineId("Профсоюзная", 5),
            metroData.getStationByNameAndLineId("Академическая", 5),
            metroData.getStationByNameAndLineId("Ленинский проспект", 5),
            metroData.getStationByNameAndLineId("Шаболовская", 5),
            metroData.getStationByNameAndLineId("Октябрьская", 5),

            metroData.getStationByNameAndLineId("Октябрьская", 6),
            metroData.getStationByNameAndLineId("Добрынинская", 6),
            metroData.getStationByNameAndLineId("Павелецкая", 6),
            metroData.getStationByNameAndLineId("Таганская", 6),
            metroData.getStationByNameAndLineId("Курская", 6),

            metroData.getStationByNameAndLineId("Курская", 3),
        ).mapNotNull { it }

        val expectedTimeInSecs = 44 * SECS_IN_MIN

        verifyShortestPath(
            startStationName = "Битцевский парк",
            startStationLineId = 12,
            finishStationName = "Курская",
            finishStationLineId = 3,
            expectedPath = expectedPath,
            expectedTimeInSecs = expectedTimeInSecs,
        )
    }

    @Test
    fun testFindShortestPath_from_ShosseEntuziastov14_to_Tulskaya9() {
        val expectedPath = listOf(
            metroData.getStationByNameAndLineId("Шоссе Энтузиастов", 9),
            metroData.getStationByNameAndLineId("Авиамоторная", 9),
            metroData.getStationByNameAndLineId("Площадь Ильича", 9),
            metroData.getStationByNameAndLineId("Марксистская", 9),

            metroData.getStationByNameAndLineId("Таганская", 6),
            metroData.getStationByNameAndLineId("Павелецкая", 6),
            metroData.getStationByNameAndLineId("Добрынинская", 6),

            metroData.getStationByNameAndLineId("Серпуховская", 7),
            metroData.getStationByNameAndLineId("Тульская", 7),
        ).mapNotNull { it }

        val expectedTimeInSecs = 21 * SECS_IN_MIN

        verifyShortestPath(
            startStationName = "Шоссе Энтузиастов",
            startStationLineId = 9,
            finishStationName = "Тульская",
            finishStationLineId = 7,
            expectedPath = expectedPath,
            expectedTimeInSecs = expectedTimeInSecs,
        )
    }

    private fun verifyShortestPath(
        startStationName: String,
        startStationLineId: Int,
        finishStationName: String,
        finishStationLineId: Int,
        expectedPath: List<Station>,
        expectedTimeInSecs: Int
    ) {
        val (actualTimeInSecs, actualPath) = metroGraph.findShortestPath(
            startStationName, startStationLineId,
            finishStationName, finishStationLineId
        )

        assertNotNull(actualPath, "Путь не найден.")
        assertTrue(actualPath!!.isNotEmpty(), "Путь не должен быть пустым.")
        assertEquals(expectedPath.size, actualPath.size, "Длина пути не совпадает с ожидаемым.")

        for (i in expectedPath.indices) {
            assertEquals(expectedPath[i].name, actualPath[i].name, "Название станции на позиции $i не совпадает.")
            assertEquals(expectedPath[i].lineId, actualPath[i].lineId, "Линия станции на позиции $i не совпадает.")
        }

        assertTimeDifferenceWithinEps(expectedTimeInSecs, actualTimeInSecs)
    }

    private fun assertTimeDifferenceWithinEps(expectedTimeInSecs: Int, actualTimeInSecs: Int) {
        val eps = 10.0

        val timeDifference = abs(expectedTimeInSecs - actualTimeInSecs)
        val maxAllowedDifference = expectedTimeInSecs * (eps / 100)

        assertTrue(timeDifference < maxAllowedDifference,
            "Время в пути отличается более чем на $eps%. Ожидалось: $expectedTimeInSecs сек, получено: $actualTimeInSecs сек.")
    }
}
