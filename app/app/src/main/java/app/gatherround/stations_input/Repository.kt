package app.gatherround.stations_input

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Единая точка доступа к REST-ресурсам, необходимым экрану выбора станций.
 *
 * Сейчас объект умеет только **скачивать SVG/HTML-файл** по произвольному URL,
 * отдавая результат через `LiveData<ResponseBody>`, чтобы ViewModel могла
 * наблюдать и автоматически освободить ресурс.
 *
 * Путь подаётся динамически в методе `downloadFileWithDynamicUrlAsync`.
 */
object Repository {

    private var service: APIService


    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val intercept = httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(intercept)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://svgshare.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        service = retrofit.create(APIService::class.java)
    }

    /**
     * Асинхронно скачивает ресурс **по любому URL** и оборачивает тело ответа
     * в `LiveData<ResponseBody>`.
     *
     * В случае ошибки (`onFailure`) LiveData остаётся `null`.
     */
    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        val responseBodyLD = MutableLiveData<ResponseBody>()
        val apiObject = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Some","response found null")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                responseBodyLD.value = response.body()
            }

        }
        service.downloadFileWithDynamicUrlAsync(
            url
        ).enqueue(apiObject)
        return responseBodyLD
    }
}

/* ----------------------------- Retrofit API --------------------------------- */

/**
 * Retrofit-контракт для скачивания сырых файлов.
 * Аннотация `@Streaming` запрещает OkHttp буферизовать весь ответ в память.
 */
interface APIService {
    @Streaming
    @GET
    fun downloadFileWithDynamicUrlAsync(
        @Url fileUrl: String
    ): Call<ResponseBody>
}

/* -------------------------- UI-extension helpers ---------------------------- */

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

/**
 * Оборачивает SVG-строку в минимальный HTML-документ с подключённым скриптом
 * `index.js`. Используется для загрузки схемы в [InteractiveMapInput].
 */
fun getHTMLBody(svgString: String) = """
    <!DOCTYPE HTML>
    <html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=4, maximum-scale=10">
        <style>
            body {
                text-align: center;
            }
        </style>
    </head>
    <body>
        <div id="div" class="container">
            $svgString
        </div>
        <script src="index.js"></script>
    </body>
    </html>
""".trimIndent()
