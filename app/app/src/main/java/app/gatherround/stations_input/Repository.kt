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

object Repository {

    private var service: APIService


    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val intercept = httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY // to check the log
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(intercept)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://svgshare.com") // No base url needed in this project, cz we will download svg image from url
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        service = retrofit.create(APIService::class.java)
    }

    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        val responseBodyLD = MutableLiveData<ResponseBody>()
        val apiObject = object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Some","response found null")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    responseBodyLD.value = response.body()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        service.downloadFileWithDynamicUrlAsync(
            url
        ).enqueue(apiObject)
        return responseBodyLD
    }
}

interface APIService {
    @Streaming
    @GET
    fun downloadFileWithDynamicUrlAsync(
        @Url fileUrl: String
    ): Call<ResponseBody>
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

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
