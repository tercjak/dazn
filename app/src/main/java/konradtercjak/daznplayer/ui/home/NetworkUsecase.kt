package konradtercjak.daznplayer.ui.home

import konradtercjak.daznplayer.model.DaznEvent
import konradtercjak.daznplayer.model.DaznItem
import konradtercjak.daznplayer.model.NetworkResponse
import konradtercjak.daznplayer.network.FakeRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import javax.inject.Inject

class NetworkUsecase @Inject constructor(
    private val networkInteractor: NetworkInteractor,
    private val viewInteractor: ViewInteractor

) {

    suspend fun onConnected(isEvents: Boolean) {
        val flow = if (isEvents) networkInteractor.getEvents() else networkInteractor.getSchedule()

        flow.flowOn(Dispatchers.IO)
            .retryWhen { e, attempt -> e is IOException && attempt < 3 }
            .collectLatest {
                onResponse(it)
            }
    }

    private fun emitConnectionError(statusCode: Int) {
        viewInteractor.emitError("Connection error $statusCode")
    }

    private fun emitConversionError(message: String?) {
        viewInteractor.emitError("Conversion Exception $message")
    }

    private fun onResponse(response: NetworkResponse<out List<DaznItem>>) {
        when (response) {
            is NetworkResponse.Success -> viewInteractor.emitSuccess(response.data)
            is NetworkResponse.HttpError<*> -> {
                if (response.statusCode == 404 || response.statusCode == 500) {
                    emitConnectionError(response.statusCode)
                }
            }
            is NetworkResponse.Error -> {
                emitConversionError(response.exception.message)
            }
        }
    }

    suspend fun initFake404() {
        FakeRepo<List<DaznEvent>>().get404()
            .flowOn(Dispatchers.IO)
            .collectLatest {
                onResponse(it)
            }
    }

}
