package konradtercjak.daznplayer.util

import android.content.Context
import android.content.res.Resources
import android.text.format.DateUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*


fun Date.isYesterday(): Boolean {
    return DateUtils.isToday(this.time + DateUtils.DAY_IN_MILLIS)
}

fun Date.isTomorrow(): Boolean {
    return DateUtils.isToday(this.time - DateUtils.DAY_IN_MILLIS)
}

fun Date.isToday(): Boolean {
    return DateUtils.isToday(this.time )
}

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun <T : Any> Fragment.autoCleaned(initializer: (() -> T)? = null): AutoCleanedValue<T> {
    return AutoCleanedValue(this, initializer)
}

fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
    FragmentViewBindingDelegate(this, viewBindingFactory)

 fun Context.showSnackbar(message: String,root: View): Snackbar {

    val snackbar = Snackbar.make(root, message, Snackbar.LENGTH_INDEFINITE)
    snackbar.view.setOnClickListener {
        snackbar.dismiss()
    }
    snackbar.show()
    return snackbar
}
