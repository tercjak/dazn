package konradtercjak.daznplayer.network


import konradtercjak.daznplayer.model.*
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.util.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter


interface DaznApi {

    companion object {
        private val httpClient = OkHttpClient.Builder()
        val retrofit: Retrofit


        init {
             val moshiBuilder = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())

            retrofit = Retrofit.Builder()
                    .client(httpClient.build())
                    .baseUrl("https://us-central1-dazn-sandbox.cloudfunctions.net/")
                .addConverterFactory(MoshiConverterFactory.create(moshiBuilder.build()).asLenient())
                .build()

        }

        fun getApi(): DaznApi {
            return retrofit.create(DaznApi::class.java)
        }
    }

    @GET("getEvents")
     suspend fun getEvents(
    ): Response<List<DaznEvent>>

    @GET("getSchedule")
    suspend fun getSchedule():Response<List<DaznSchedule>>

}

