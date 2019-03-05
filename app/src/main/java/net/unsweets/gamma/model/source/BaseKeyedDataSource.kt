package net.unsweets.gamma.model.source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import net.unsweets.gamma.model.entity.PnutResponse
import net.unsweets.gamma.model.net.PnutRepository
import net.unsweets.gamma.model.net.PnutService
import net.unsweets.gamma.model.net.PnutServiceOption
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseKeyedDataSource<T>(context: Context) : ItemKeyedDataSource<String, T>() {
    val pnutService: PnutService = PnutRepository(context).pnutService
    val pnut: PnutServiceOption = PnutServiceOption(pnutService, 20)
    // TODO: replace with preference's value
    private val pageSize = 20
    private val pagingConfig = PagedList.Config.Builder().setInitialLoadSizeHint(pageSize).setPageSize(pageSize).build()

    sealed class State {
        object None : State()
        object Pending : State()
        object Resolve : State()
        data class Reject(val t: Throwable?) : State()
    }

    val state = MutableLiveData<State>().apply { postValue(State.None) }


    fun <T> Call<PnutResponse<List<T>>>.toList(callback: (list: List<T>) -> Unit) {
        this.enqueue(object : Callback<PnutResponse<List<T>>> {
            override fun onFailure(call: Call<PnutResponse<List<T>>>, t: Throwable) {
                state.postValue(State.Reject(t))
            }

            override fun onResponse(call: Call<PnutResponse<List<T>>>, response: Response<PnutResponse<List<T>>>) {
                val body = response.body() ?: return onFailure(call, Throwable("body is empty"))
                callback(body.data)
                state.postValue(State.Resolve)
            }
        })
    }

    protected fun modMax(params: LoadParams<String>) =
        pnut.defaultPaging.copy(count = params.requestedLoadSize, maxId = params.key).toMap()

    protected fun modMin(params: LoadParams<String>) =
        pnut.defaultPaging.copy(count = params.requestedLoadSize, minId = params.key).toMap()

    protected fun error() = Throwable("You must set extra option")


}