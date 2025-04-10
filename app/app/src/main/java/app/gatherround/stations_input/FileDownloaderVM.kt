package app.gatherround.stations_input

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import okhttp3.ResponseBody

class FileDownloaderVM (application: Application) : AndroidViewModel(application) {
    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        return Repository.downloadFileFromServer(url)
    }
}