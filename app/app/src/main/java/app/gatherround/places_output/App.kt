package app.gatherround.places_output

import android.app.Application
import app.gatherround.BuildConfig
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPS_TOKEN)
    }

}