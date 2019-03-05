package net.unsweets.gamma.ui.util

import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import net.unsweets.gamma.R
import net.unsweets.gamma.model.entity.PnutResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Snackbar.error(): Snackbar {
    val view = this.view
    val bgColor = ContextCompat.getColor(view.context, R.color.colorError)
    view.setBackgroundColor(bgColor)
    val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.setTextColor(Color.WHITE)
    return this
}

fun <T> Call<PnutResponse<T>>.toLiveData(): LiveData<Promise<PnutResponse<T>>> {
    val res = MutableLiveData<Promise<PnutResponse<T>>>().apply { value = Promise.Pending() }
    this.enqueue(object : Callback<PnutResponse<T>> {
        override fun onFailure(call: Call<PnutResponse<T>>, t: Throwable) {
            LogUtil.e(t.message)
            res.postValue(Promise.Failure(t))
        }

        override fun onResponse(call: Call<PnutResponse<T>>, response: Response<PnutResponse<T>>) {
            val body = response.body() ?: return onFailure(call, Throwable("body is empty"))
            res.postValue(Promise.Success(body))
        }
    })
    return res
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
