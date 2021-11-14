package konradtercjak.daznplayer.model

sealed interface NetworkResponse<T> {
    class HttpError<T>(val statusCode: Int?) : NetworkResponse<T>
    class Success<T>(val data: T) : NetworkResponse<T>
    class Error<T>(val exception: Throwable) : NetworkResponse<T>

    companion object {
        fun <T> success(data: T): Success<T> {
            return Success(data)
        }

        fun <T> error(statusCode: Int?): HttpError<T> {
            return HttpError(statusCode)
        }

        fun <T> error(throwable: Throwable): Error<T> {
            return Error(throwable)
        }
    }
}
