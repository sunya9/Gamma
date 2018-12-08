package net.unsweets.gamma.util

import android.graphics.Color
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import net.unsweets.gamma.api.PnutResponse
import net.unsweets.gamma.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Snackbar.error(): Snackbar {
    val view = this.view
    val bgColor = ContextCompat.getColor(view.context, R.color.error_color_material_dark)
    view.setBackgroundColor(bgColor)
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(Color.WHITE)
    return this
}

fun <T> Response<T>.applyToLiveData(liveData: MutableLiveData<T>) {
    // TODO: error handling
    val body = this.body() ?: return
    liveData.value = body
}

fun <T> Call<PnutResponse<T>>.then(then: (result: PnutResponse<T>) -> Unit) {
    this.enqueue(object: Callback<PnutResponse<T>> {
        override fun onFailure(call: Call<PnutResponse<T>>, t: Throwable) {
            Log.e("Error", t.message)
            catch(t)
        }

        override fun onResponse(call: Call<PnutResponse<T>>, response: Response<PnutResponse<T>>) {
            val data = response.body() ?: return onFailure(call, Throwable("Unknown error"))
            then(data)
        }
    })
}

var <T> Callback<T>.catch: (t: Throwable) -> Unit
    get() = { Unit }
    set(_) {}
