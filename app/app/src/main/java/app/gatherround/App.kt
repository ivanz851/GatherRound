package app.gatherround

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("1e290826-198f-483d-90a5-638e7122ef51")
    }

}