package net.unsweets.gamma.util

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.text.format.DateFormat
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.suspendCancellableCoroutine
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun Snackbar.showAsError() {
    val view = this.view
    val bgColor = ContextCompat.getColor(view.context, R.color.colorError)
    view.setBackgroundColor(bgColor)
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(Color.WHITE)
    show()
}

fun Snackbar.oneline(): Snackbar {
    val view = this.view
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.maxLines = 1
    textView.ellipsize = TextUtils.TruncateAt.END
    return this
}

suspend fun <T> Call<PnutResponse<T>>.await(): PnutResponse<T> = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback<PnutResponse<T>> {
        override fun onFailure(call: Call<PnutResponse<T>>, t: Throwable) {
            if (!cont.isCancelled) {
                cont.resumeWithException(t)
            }
        }

        override fun onResponse(call: Call<PnutResponse<T>>, response: Response<PnutResponse<T>>) {
            LogUtil.e(response.toString())
            response.body()?.let { cont.resume(it) }
                ?: response.errorBody()?.let { cont.cancel(Throwable(it.string())) }

        }
    })

    cont.invokeOnCancellation {
        cancel()
    }
}

fun Boolean.toInt(): Int = if (this) 1 else 0

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun Date.toFormatString(context: Context?): String {
    val dateFormatTemplate = context?.getString(R.string.file_date_format_template)
    return DateFormat.format(dateFormatTemplate, this).toString()
}