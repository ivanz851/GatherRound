package app.gatherround.places_output

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.gatherround.places.Place
import app.gatherround.places.PlacesData
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import android.net.Uri
import android.widget.Toast
import app.gatherround.R
import app.gatherround.metro.Location
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider



class PlacesOutputMap : ComponentActivity() {
    @Composable
    fun MapScreen(
        places: List<Place>,
        mapView: MapView,
        onBackClick: () -> Unit
    ) {
        val context = LocalContext.current
        var selectedPlaceId by remember { mutableStateOf<Int?>(null) }

        Box(Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { mapView  },
                update  = { mv ->
                    val mapObjects = mv.map.mapObjects
                    val pin = ImageProvider.fromResource(context, R.drawable.ic_custom_pin)

                    mapObjects.clear()

                    for (p in places) {
                        val lat = p.coords?.lat
                        val lon = p.coords?.lon
                        if (lat != null && lon != null) {
                            val point = Point(lat, lon)
                            mapObjects.addPlacemark(point, pin).apply {
                                setIconStyle(
                                    IconStyle().apply {
                                        scale = 2f
                                        anchor = PointF(0.5f, 1f)
                                    }
                                )
                                addTapListener { _, _ ->
                                    selectedPlaceId = p.id
                                    true
                                }
                            }
                        }
                    }

                    if (places.isNotEmpty()) {
                        val first = places.first().coords!!
                        mv.map.move(
                            CameraPosition(
                                Point(first.lat!!, first.lon!!),
                                14f, 0f, 0f
                            )
                        )
                    }
                }
            )

            places.find { it.id == selectedPlaceId }?.let { place ->
                AlertDialog(
                    onDismissRequest = { selectedPlaceId = null },
                    title = { Text(place.title) },
                    text  = { Text("Построить маршрут до:\n${place.address}") },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedPlaceId = null

                            val latFrom = stationLat
                            val lonFrom = stationLon
                            val latTo   = place.coords!!.lat!!
                            val lonTo   = place.coords.lon!!

                            val url = "https://yandex.ru/maps/?" +
                                    "rtext=$latFrom,$lonFrom~$latTo,$lonTo" +
                                    "&rtt=pd"

                            val uri = Uri.parse(url)

                            val mapsIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                setPackage("ru.yandex.yandexmaps")
                            }

                            if (mapsIntent.resolveActivity(packageManager) != null) {
                                startActivity(mapsIntent)
                            } else {
                                startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }
                        }) { Text("Построить маршрут") }
                    },
                    dismissButton = {
                        TextButton({ selectedPlaceId = null }) { Text("Отмена") }
                    }
                )
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) { Text("Назад") }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        mapView = MapView(this)

        stationLat = intent.getDoubleExtra("station_lat", 0.0)
        stationLon = intent.getDoubleExtra("station_lon", 0.0)
        val jsonString = intent.getStringExtra("places_json") ?: ""
        val places = PlacesData().parsePlaces(jsonString)

        setContent {
            MapScreen(
                places = places,
                mapView = mapView,
                onBackClick = { finish() }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private lateinit var mapView: MapView
    private var stationLat: Double = 0.0
    private var stationLon: Double = 0.0
}
