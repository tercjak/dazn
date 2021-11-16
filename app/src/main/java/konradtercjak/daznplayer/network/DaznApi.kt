package konradtercjak.daznplayer.network


import konradtercjak.daznplayer.model.*
import retrofit2.Response
import retrofit2.http.GET


interface DaznApi {

    @GET("getEvents")
     suspend fun getEvents(
    ): Response<List<DaznEvent>>

    @GET("getSchedule")
    suspend fun getSchedule():Response<List<DaznSchedule>>

}

