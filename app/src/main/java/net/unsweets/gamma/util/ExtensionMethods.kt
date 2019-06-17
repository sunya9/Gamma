package net.unsweets.gamma.util

import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.suspendCancellableCoroutine
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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


fun <T> Call<PnutResponse<T>>.toLiveData(mutableLiveDataValue: MutableLiveData<T>) {
    this.enqueue(object : Callback<PnutResponse<T>> {
        override fun onFailure(call: Call<PnutResponse<T>>, t: Throwable) {
            LogUtil.e(t.message)
        }

        override fun onResponse(call: Call<PnutResponse<T>>, response: Response<PnutResponse<T>>) {
            val body = response.body() ?: return onFailure(call, Throwable("body is empty"))
            mutableLiveDataValue.postValue(body.data)
        }
    })
}

fun <T> Call<PnutResponse<List<T>>>.toArrayListLiveData(mutableLiveDataValue: MutableLiveData<ArrayList<T>>) {
    this.enqueue(object : Callback<PnutResponse<List<T>>> {
        override fun onFailure(call: Call<PnutResponse<List<T>>>, t: Throwable) {
            LogUtil.e(t.message)
        }

        override fun onResponse(call: Call<PnutResponse<List<T>>>, response: Response<PnutResponse<List<T>>>) {
            val body = response.body() ?: return onFailure(call, Throwable("body is empty"))
            mutableLiveDataValue.postValue(ArrayList(body.data))
        }
    })
}

suspend fun <T> Call<PnutResponse<T>>.await(): PnutResponse<T> = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback<PnutResponse<T>> {
        override fun onFailure(call: Call<PnutResponse<T>>, t: Throwable) {
            if (!cont.isCancelled) {
                cont.resumeWithException(t)
            }
        }

        override fun onResponse(call: Call<PnutResponse<T>>, response: Response<PnutResponse<T>>) {
            Log.e("response", response.toString())
            val body = response.body() ?: return cont.resumeWithException(Throwable())
            cont.resume(body)
        }
    })

    cont.invokeOnCancellation {
        cancel()
    }
}

fun Boolean.toInt(): Int = if(this) 1 else 0