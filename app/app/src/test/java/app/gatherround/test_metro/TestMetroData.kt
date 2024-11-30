package app.gatherround.test_metro

import app.gatherround.loadMetroDataFromFile
import app.gatherround.metro.MetroData
import app.gatherround.metro.getSchemeMetadataPath
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class TestMetroData {
    private lateinit var metroData: MetroData

    @BeforeEach
    fun setUp() {
        println("Current working directory: ${System.getProperty("user.dir")}")
        metroData = loadMetroDataFromFile(getSchemeMetadataPath())
    }

    @Test
    fun testGetStationByNameAndLineId_InvalidLineId() {
        val station = metroData.getStationByNameAndLineId("Tretyakovskaya", 5)
        assertNull(station, "Станция не должна быть найдена на этой линии")
    }
}