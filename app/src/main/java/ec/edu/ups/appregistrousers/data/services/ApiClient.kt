package ec.edu.ups.appregistrousers.data.services

import android.content.Context
import ec.edu.ups.appregistrousers.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiClient {
    private  lateinit var apiService: ApiService
    fun getApiService(context: Context):ApiService{
        if(!::apiService.isInitialized){
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }

    fun getPythonApiService(context: Context):ApiService{
        if(!::apiService.isInitialized){
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC)

            // Esto arregla un error que sucede por
            // el límite de tiempo que tiene el client de Retrofit,
            // se crea un Http client con un límite de tiempo mayor
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_PYTHON)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }
}