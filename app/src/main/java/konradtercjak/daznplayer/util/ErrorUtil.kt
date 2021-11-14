package konradtercjak.daznplayer.util

import konradtercjak.daznplayer.model.*
import retrofit2.*
import java.lang.Exception


object ErrorUtils {
    fun<T> parseError(response: Response<T>): NetworkResponse.HttpError<T> {
        return NetworkResponse.error(response.code())
    }
    fun<T> parseError(exception: Throwable): NetworkResponse.Error<T> {
        return NetworkResponse.error(exception)
    }
}