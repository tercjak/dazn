package konradtercjak.daznplayer.ui.home

import androidx.lifecycle.*
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import dagger.hilt.android.lifecycle.HiltViewModel
import konradtercjak.daznplayer.R
import konradtercjak.daznplayer.model.*
import konradtercjak.daznplayer.network.DaznApi
import konradtercjak.daznplayer.util.ErrorUtils.parseError
import konradtercjak.daznplayer.util.px
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class DaznViewModel  @Inject constructor(private val api: DaznApi):ViewModel() {
    private var _clickedUrl = MutableLiveData<String>()
    val clickedUrl: LiveData<String>
        get() {
            return _clickedUrl
        }

    fun getEvents(): Flow<NetworkResponse<List<DaznEvent>>> {
        return flow {

            try {
                var response = api.getEvents()

                if (response.isSuccessful && response.body() != null)
                    emit(NetworkResponse.success(response.body()!!.sortedBy { it.date }))
                else
                    emit(parseError(response))

                //wait 5sec and retry on 404 or 500
                while ((response.code() == 404 || response.code() == 500)) {
                    delay(5000)
                    response = api.getEvents()
                    if (response.isSuccessful && response.body() != null)
                        emit(NetworkResponse.success(response.body()!!.sortedBy { it.date }))
                    else
                        emit(parseError(response))
                }
            } catch (e: IOException) {
                throw e
            } catch (t: Throwable) {
                emit(parseError(t))

            }
        }
    }


    fun getSchedule(): Flow<NetworkResponse<List<DaznSchedule>>> {
        return flow {
            while (true) {
                try {
                    val response = api.getSchedule()

                    if (response.isSuccessful && response.body() != null)
                        emit(NetworkResponse.success(response.body()!!.sortedBy { it.date }))
                    else
                        emit(parseError(response))

                    delay(30000)
                } catch (e: IOException) {
                    throw e
                } catch (t: Throwable) {
                    emit(parseError(t))

                }
            }
        }
    }

    fun bind(url: String, holder: DaznAdapter.PhotoHolder, item: DaznItem) {

        holder.disposableImage = holder.binding.photoIv.load(url) {
            crossfade(true)
            diskCachePolicy(CachePolicy.ENABLED)
            placeholder(R.drawable.placeholder)
            transformations(RoundedCornersTransformation(12.px.toFloat()))
        }

        if (item is DaznEvent) {
            holder.binding.root.setOnClickListener {
                _clickedUrl.value = item.videoUrl
            }
        }
    }

}