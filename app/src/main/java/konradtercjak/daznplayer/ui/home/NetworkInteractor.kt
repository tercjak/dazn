package konradtercjak.daznplayer.ui.home

import konradtercjak.daznplayer.model.DaznEvent
import konradtercjak.daznplayer.model.DaznSchedule
import konradtercjak.daznplayer.model.NetworkResponse
import konradtercjak.daznplayer.network.DaznApi
import konradtercjak.daznplayer.util.ErrorUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class NetworkInteractor @Inject constructor(private val api: DaznApi) {

    fun getEvents(): Flow<NetworkResponse<List<DaznEvent>>> {
        //TODO TOO much responsibilty retrying on 404 or 500 is UseCase responsibility
        // The same as sorting

        return flow {

            try {
                var response = api.getEvents()

                if (response.isSuccessful && response.body() != null)
                    emit(NetworkResponse.success(response.body()!!.sortedBy { it.date }))
                else
                    emit(ErrorUtils.parseError(response))

                //wait 5sec and retry on 404 or 500
                while ((response.code() == 404 || response.code() == 500)) {
                    delay(5000)
                    response = api.getEvents()
                    if (response.isSuccessful && response.body() != null)
                        emit(NetworkResponse.success(response.body()!!.sortedBy { it.date }))
                    else
                        emit(ErrorUtils.parseError(response))
                }
            } catch (e: IOException) {
                throw e
            } catch (t: Throwable) {
                emit(ErrorUtils.parseError(t))

            }
        }
    }

    fun getSchedule(): Flow<NetworkResponse<List<DaznSchedule>>> {
        //TODO TOO much responsibilty retrying on 404 or 500 is UseCase responsibility
        // The same as sorting and refreshing
        return flow {
            while (true) {
                try {
                    val response = api.getSchedule()

                    if (response.isSuccessful && response.body() != null)
                        emit(NetworkResponse.success(response.body()!!.sortedBy { it.date }))
                    else
                        emit(ErrorUtils.parseError(response))

                    delay(30000)
                } catch (e: IOException) {
                    throw e
                } catch (t: Throwable) {
                    emit(ErrorUtils.parseError(t))

                }
            }
        }
    }

}