package app.gatherround.test_metro

import app.gatherround.graph.Dijkstra
import app.gatherround.loadMetroDataFromFile
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import app.gatherround.metro.MetroStation
import app.gatherround.metro.getSchemeMetadataPath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.abs

class TestMetroGraph {
    private lateinit var metroData: MetroData
    private lateinit var metroGraph: MetroGraph

    @BeforeEach
    fun setUp() {
        metroData = loadMetroDataFromFile("C:\\\\Users\\\\test\\\\user\\\\ProjectSeminar2024-25\\\\GatherRound\\\\app\\\\app\\\\src\\\\main\\\\resources\\\\get-scheme-metadata.json")
        metroGraph = MetroGraph(metroData)
    }

    @Test
    fun testDijkstraAlgorithmReachesAllStations() {
        val startStation = metroData.getStationByNameAndLineId("Tretyakovskaya", 6)

        val dijkstra = Dijkstra<MetroStation>()
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

        val expectedTimeInSecs = 2820

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

        val expectedTimeInSecs = 2820

        verifyShortestPath(
            startStationName = "Bitsevsky Park",
            startStationLineId = 12,
            finishStationName = "Kurskaya",
            finishStationLineId = 3,
            expectedPath = expectedPath,
            expectedTimeInSecs = expectedTimeInSecs,
        )
    }

    private fun verifyShortestPath(
        startStationName: String,
        startStationLineId: Int,
        finishStationName: String,
        finishStationLineId: Int,
        expectedPath: List<MetroStation>,
        expectedTimeInSecs: Int
    ) {
        val (actualTimeInSecs, actualPath) = metroGraph.findShortestPath(
            startStationName, startStationLineId,
            finishStationName, finishStationLineId
        )

        println(actualPath)

        println(expectedPath)

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
        val eps = 8.0

        val timeDifference = abs(expectedTimeInSecs - actualTimeInSecs)
        val maxAllowedDifference = expectedTimeInSecs * (eps / 100)

        assertTrue(timeDifference < maxAllowedDifference,
            "Время в пути отличается более чем на $eps%. Ожидалось: $expectedTimeInSecs сек, получено: $actualTimeInSecs сек.")
    }
}
