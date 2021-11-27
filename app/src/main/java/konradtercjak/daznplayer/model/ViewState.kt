package konradtercjak.daznplayer.model

sealed  interface ViewState<T>
class LOADING<T> : ViewState<T>
class SUCCESS<T>(val data:List<T>) :ViewState<T>
class ERROR<T>(val message:String) :ViewState<T>
class ERROR_DIALOG<T>(val title:String,val message:String) :ViewState<T>