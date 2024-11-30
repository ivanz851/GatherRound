package app.gatherround.test_metro

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
        metroData = loadMetroDataFromFile(getSchemeMetadataPath())
        metroGraph = MetroGraph(metroData)
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
        val eps = 5.0

        val timeDifference = abs(expectedTimeInSecs - actualTimeInSecs)
        val maxAllowedDifference = expectedTimeInSecs * (eps / 100)

        assertTrue(timeDifference < maxAllowedDifference,
            "Время в пути отличается более чем на $eps%. Ожидалось: $expectedTimeInSecs сек, получено: $actualTimeInSecs сек.")
    }
}
