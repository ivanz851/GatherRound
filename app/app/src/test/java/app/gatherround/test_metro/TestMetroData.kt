package app.gatherround.test_metro

import app.gatherround.metro.MetroData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class TestMetroData {
    private val metroData = MetroData().loadMetroDataFromFile()
    @BeforeEach
    fun setUp() {

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