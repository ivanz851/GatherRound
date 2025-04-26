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
import app.gatherround.R
import com.yandex.mapkit.map.IconStyle
import com.yandex.runtime.image.ImageProvider

class PlacesOutputMap : ComponentActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("1e290826-198f-483d-90a5-638e7122ef51")
        MapKitFactory.initialize(this)

        val jsonString = intent.getStringExtra("places_json") ?: ""
        val places = PlacesData().parsePlaces(jsonString)

        setContent {
            MapScreen(places = places) {
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        if (::mapView.isInitialized) mapView.onStart()
    }

    override fun onStop() {
        if (::mapView.isInitialized) mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    @Composable
    fun MapScreen(places: List<Place>, onBackClick: () -> Unit) {
        val context = LocalContext.current
        var selectedPlaceId by remember { mutableStateOf<String?>(null) }

        val mapView = remember { MapView(context) }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    mapView
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                mapView ->
                val mapObjects = mapView.map.mapObjects
                val imageProvider = ImageProvider.fromResource(context, R.drawable.ic_custom_pin)

                mapObjects.clear()

                for (place in places) {
                    if (place.coords?.lat != null &&
                        place.coords.lon != null) {
                        val point = Point(place.coords.lat, place.coords.lon)
                        val placemark = mapObjects.addPlacemark().apply {
                            geometry = point
                            setIcon(imageProvider)
                            setIconStyle(
                                IconStyle().apply {
                                    scale = 2.0f
                                    anchor = PointF(0.5f, 1.0f)
                                }
                            )
                            addTapListener { _, _ ->
                                selectedPlaceId = "${place.id}"
                                true
                            }
                        }
                    }
                }

                if (places.isNotEmpty()) {
                    val first = places.first()
                    val lat = first.coords?.lat ?: 0.0
                    val lon = first.coords?.lon ?: 0.0
                    mapView.map.move(
                        CameraPosition(Point(lat, lon), 14.0f, 0.0f, 0.0f)
                    )
                }
            }

            val selectedPlace = places.find {
                "${it.id}" == selectedPlaceId
            }

            selectedPlace?.let { place ->
                AlertDialog(
                    onDismissRequest = { selectedPlaceId = null },
                    title = { Text("Место: ${place.title}") },
                    text = { Text("Построить маршрут до:\n${place.address}") },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedPlaceId = null
                            // TODO: Здесь можно вызвать навигацию
                            val gmmIntentUri = Uri.parse("google.navigation:q=${place.id}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            context.startActivity(mapIntent)
                        }) {
                            Text("Построить маршрут")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedPlaceId = null }) {
                            Text("Отмена")
                        }
                    }
                )
            }

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Назад")
            }
        }
    }
}
