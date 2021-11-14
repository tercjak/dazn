package konradtercjak.daznplayer.network

import konradtercjak.daznplayer.model.*
import konradtercjak.daznplayer.util.ErrorUtils
import kotlinx.coroutines.flow.*
import okhttp3.ResponseBody
import retrofit2.Response

class FakeRepo<T> {
    fun get404(): Flow<NetworkResponse<T>> {
        return flow {

            val response = Response.error<T>(404, ResponseBody.create(null,""))

                emit(ErrorUtils.parseError(response))

        }
    }

    fun get500(): Flow<NetworkResponse<T>> {
        return flow {

            val response = Response.error<T>(500, ResponseBody.create(null,""))

            emit(ErrorUtils.parseError(response))

        }
    }
}