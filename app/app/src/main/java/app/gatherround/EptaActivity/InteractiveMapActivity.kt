package app.gatherround.EptaActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.gatherround.metro.MetroData
import app.gatherround.metro.MetroGraph
import com.sam43.svginteractiondemo.FileDownloaderVM
import com.sam43.svginteractiondemo.getHTMLBody

class InteractiveMapActivity : ComponentActivity() {

    private val fileDownloaderVM: FileDownloaderVM by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val metroData = MetroData.loadFromAssets(applicationContext, "get-scheme-metadata.json")
        val stationsNames = metroData.stationsNames
        val graph = MetroGraph(metroData)

        setContent {
            MaterialTheme {
                val htmlContent = remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val url = "https://raw.githubusercontent.com/ivanz851/GatherRound/refs/heads/implement_interactve_metro_map/app/app/src/main/assets/initial.svg"
                    try {
                        fileDownloaderVM.downloadFileFromServer(url).observe(this@InteractiveMapActivity) { response ->
                            val svg = response.string()
                            htmlContent.value = getHTMLBody(svg)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                InteractiveMapScreen(
                    metroData = metroData,
                    stationsNames = stationsNames,
                    graph = graph,
                    htmlContent = htmlContent.value
                )
            }
        }
    }
}
