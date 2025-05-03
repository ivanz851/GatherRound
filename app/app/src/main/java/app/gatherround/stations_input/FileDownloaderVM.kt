package app.gatherround.stations_input

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import okhttp3.ResponseBody

/**
 * ViewModel-обёртка над [Repository.downloadFileFromServer].
 */
class FileDownloaderVM (application: Application) : AndroidViewModel(application) {
    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        return Repository.downloadFileFromServer(url)
    }
}