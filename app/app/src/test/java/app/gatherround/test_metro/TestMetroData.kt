package app.gatherround.test_metro

import app.gatherround.loadMetroDataFromFile
import app.gatherround.metro.MetroData
import app.gatherround.metro.getSchemeMetadataPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class TestMetroData {
    private lateinit var metroData: MetroData

    @BeforeEach
    fun setUp() {
        metroData = loadMetroDataFromFile("C:\\\\Users\\\\test\\\\user\\\\ProjectSeminar2024-25\\\\GatherRound\\\\app\\\\app\\\\src\\\\main\\\\resources\\\\get-scheme-metadata.json")
    }

    @Test
    fun testGetStationByNameAndLineId_InvalidLineId() {
        val station = metroData.getStationByNameAndLineId("Tretyakovskaya", 5)
        assertNull(station, "Станция не должна быть найдена на этой линии")
    }

    @Test
    fun testGetStationByNameAndLineId() {
        val station = metroData.getStationByNameAndLineId("Kiyevskaya", 5)

        assertNotNull(station, "Станция не найдена.")
        assertEquals("Kiyevskaya", station!!.name, "Название станции не совпадает.")
        assertEquals(5, station.lineId, "Идентификатор линии станции не совпадает.")
    }
}