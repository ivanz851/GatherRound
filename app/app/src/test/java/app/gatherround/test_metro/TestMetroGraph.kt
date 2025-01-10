package app.gatherround.test_metro

import app.gatherround.graph.Dijkstra
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
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
    fun testDijkstraAlgorithmReachesAllStations() {
        val startStation = metroData.getStationByNameAndLineId("Tretyakovskaya", 6)

        val dijkstra = Dijkstra<Station>()
        val (distances, ancestors) = dijkstra.calcShortestPathsFromVertex(metroGraph, startStation!!)

        for ((station, distance) in distances) {
            assertTrue(distance != Int.MAX_VALUE, "Станция ${station.name} недостижима.")
        }

        for ((station, predecessor) in ancestors) {
            if (station != startStation) {
                assertNotNull(predecessor, "У станции ${station.name} нет предка.")
            }
        }
    }

    @Test
    fun testFindShortestPath_from_ParkPobedy3_to_Kotelniki7() {
        val expectedPath = listOf(
            metroData.getStationByNameAndLineId("Park Pobedy", 3),
            metroData.getStationByNameAndLineId("Kiyevskaya", 3),

            metroData.getStationByNameAndLineId("Kiyevskaya", 5),
            metroData.getStationByNameAndLineId("Park Kultury", 5),
            metroData.getStationByNameAndLineId("Oktyabrskaya", 5),
            metroData.getStationByNameAndLineId("Dobryninskaya", 5),
            metroData.getStationByNameAndLineId("Paveletskaya", 5),
            metroData.getStationByNameAndLineId("Taganskaya", 5),

            metroData.getStationByNameAndLineId("Taganskaya", 7),
            metroData.getStationByNameAndLineId("Proletarskaya", 7),
            metroData.getStationByNameAndLineId("Volgogradsky Prospekt", 7),
            metroData.getStationByNameAndLineId("Tekstilshchiki", 7),
            metroData.getStationByNameAndLineId("Kuzminki", 7),
            metroData.getStationByNameAndLineId("Ryazanskiy Prospekt", 7),
            metroData.getStationByNameAndLineId("Vykhino", 7),
            metroData.getStationByNameAndLineId("Lermontovsky Prospekt", 7),
            metroData.getStationByNameAndLineId("Zhulebino", 7),
            metroData.getStationByNameAndLineId("Kotelniki", 7),
        ).mapNotNull { it }

        val expectedTimeInSecs = 47 * SECS_IN_MIN

        verifyShortestPath(
            startStationName = "Park Pobedy",
            startStationLineId = 3,
            finishStationName = "Kotelniki",
            finishStationLineId = 7,
            expectedPath = expectedPath,
            expectedTimeInSecs = expectedTimeInSecs,
        )
    }

    @Test
    fun testFindShortestPath_from_BitsevskyPark12_to_Kurskaya3() {
        val expectedPath = listOf(
            metroData.getStationByNameAndLineId("Bitsevsky Park", 12),

            metroData.getStationByNameAndLineId("Novoyasenevskaya", 6),
            metroData.getStationByNameAndLineId("Yasenevo", 6),
            metroData.getStationByNameAndLineId("Tyoplyi Stan", 6),
            metroData.getStationByNameAndLineId("Konkovo", 6),
            metroData.getStationByNameAndLineId("Belyayevo", 6),
            metroData.getStationByNameAndLineId("Kaluzhskaya", 6),
            metroData.getStationByNameAndLineId("Noviye Cheryomushki", 6),
            metroData.getStationByNameAndLineId("Profsoyuznaya", 6),
            metroData.getStationByNameAndLineId("Akademicheskaya", 6),
            metroData.getStationByNameAndLineId("Leninsky Prospekt", 6),
            metroData.getStationByNameAndLineId("Shabolovskaya", 6),
            metroData.getStationByNameAndLineId("Oktyabrskaya", 6),

            metroData.getStationByNameAndLineId("Oktyabrskaya", 5),
            metroData.getStationByNameAndLineId("Dobryninskaya", 5),
            metroData.getStationByNameAndLineId("Paveletskaya", 5),
            metroData.getStationByNameAndLineId("Taganskaya", 5),
            metroData.getStationByNameAndLineId("Kurskaya", 5),

            metroData.getStationByNameAndLineId("Kurskaya", 3),
        ).mapNotNull { it }

        val expectedTimeInSecs = 44 * SECS_IN_MIN

        verifyShortestPath(
            startStationName = "Bitsevsky Park",
            startStationLineId = 12,
            finishStationName = "Kurskaya",
            finishStationLineId = 3,
            expectedPath = expectedPath,
            expectedTimeInSecs = expectedTimeInSecs,
        )
    }

    @Test
    fun testFindShortestPath_from_ShosseEntuziastov14_to_Tulskaya9() {
        val expectedPath = listOf(
            metroData.getStationByNameAndLineId("Shosse Entuziastov", 8),
            metroData.getStationByNameAndLineId("Aviamotornaya", 8),
            metroData.getStationByNameAndLineId("Ploshchad Ilicha", 8),
            metroData.getStationByNameAndLineId("Marksistskaya", 8),

            metroData.getStationByNameAndLineId("Taganskaya", 5),
            metroData.getStationByNameAndLineId("Paveletskaya", 5),
            metroData.getStationByNameAndLineId("Dobryninskaya", 5),

            metroData.getStationByNameAndLineId("Serpukhovskaya", 9),
            metroData.getStationByNameAndLineId("Tulskaya", 9),
        ).mapNotNull { it }

        val expectedTimeInSecs = 21 * SECS_IN_MIN

        verifyShortestPath(
            startStationName = "Shosse Entuziastov",
            startStationLineId = 8,
            finishStationName = "Tulskaya",
            finishStationLineId = 9,
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

        for (element in actualPath!!) {
            println("${element.name}, ${element.lineId}")
        }

        println(expectedPath)

        assertNotNull(actualPath, "Путь не найден.")
        assertTrue(actualPath.isNotEmpty(), "Путь не должен быть пустым.")
        assertEquals(expectedPath.size, actualPath.size, "Длина пути не совпадает с ожидаемым.")

        for (i in expectedPath.indices) {
            assertEquals(expectedPath[i].name, actualPath[i].name, "Название станции на позиции $i не совпадает.")
            assertEquals(expectedPath[i].lineId, actualPath[i].lineId, "Линия станции на позиции $i не совпадает.")
        }

        assertTimeDifferenceWithinEps(expectedTimeInSecs, actualTimeInSecs)
    }

    private fun assertTimeDifferenceWithinEps(expectedTimeInSecs: Int, actualTimeInSecs: Int) {
        val eps = 5.0

        val timeDifference = abs(expectedTimeInSecs - actualTimeInSecs)
        val maxAllowedDifference = expectedTimeInSecs * (eps / 100)

        assertTrue(timeDifference < maxAllowedDifference,
            "Время в пути отличается более чем на $eps%. Ожидалось: $expectedTimeInSecs сек, получено: $actualTimeInSecs сек.")
    }

    /*
    @Test
    fun testLargeLists() {
        val sortedDistances = listOf(
            listOf(
                50 to 1,
                60 to 2,
                70 to 3,
                80 to 4,
                90 to 5,
                100 to 6,
                110 to 7,
                120 to 8,
                130 to 9,
                140 to 10,
                150 to 11
            ),
            listOf(
                60 to 1,
                70 to 2,
                80 to 3,
                90 to 4,
                100 to 5,
                110 to 6,
                120 to 7,
                130 to 8,
                140 to 9,
                150 to 11,
                160 to 12
            ),
            listOf(
                55 to 1,
                65 to 2,
                75 to 3,
                85 to 4,
                95 to 5,
                105 to 6,
                115 to 7,
                125 to 8,
                135 to 9,
                145 to 10,
                150 to 11,
                155 to 12,
                156 to 13,
                157 to 14,
                1000 to 15,
            )
        )
        val t = 160
        val result = metroGraph.hasCommonStation(sortedDistances, t)

        assert(result.first == true) { "Ошибка: Ожидалась общая станция, но результат ${result.first}" }
        assert(result.second == 11) { "Ошибка: Ожидалась станция с ID 11, но результат ${result.second}" }
    }
    */

}
