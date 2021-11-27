package konradtercjak.daznplayer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.load
import coil.request.CachePolicy
import coil.transform.RoundedCornersTransformation
import konradtercjak.daznplayer.R
import konradtercjak.daznplayer.model.*
import konradtercjak.daznplayer.util.px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewInteractor {
   private val scope = MainScope()

    private val _state = MutableStateFlow<ViewState<DaznItem>>(LOADING())
    val state: StateFlow<ViewState<DaznItem>>
        get() = _state

    private var _clickedUrl = MutableLiveData<String>()
    val clickedUrl: LiveData<String>
        get() {
            return _clickedUrl
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

    fun emitError(message: String) {
        scope.launch(Dispatchers.IO) {
            _state.emit(ERROR(message))
        }
    }

    fun emitSuccess(items: List<DaznItem>) {
        scope.launch(Dispatchers.IO) {
            _state.emit(SUCCESS(items))
        }
    }

}