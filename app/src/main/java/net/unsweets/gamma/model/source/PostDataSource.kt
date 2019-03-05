package net.unsweets.gamma.model.source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagedList
import net.unsweets.gamma.model.entity.PnutResponse
import net.unsweets.gamma.model.entity.Post
import net.unsweets.gamma.model.net.PnutRepository
import net.unsweets.gamma.model.net.PnutService
import net.unsweets.gamma.model.net.PnutServiceOption
import net.unsweets.gamma.ui.util.LogUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDataSource(
    context: Context,
    private val streamType: PnutRepository.StreamType,
    private val extra: String?
) : ItemKeyedDataSource<String, Post>() {
    override fun getKey(item: Post): String = item.id

    //    override fun getKey(item: Post): String = item.id
    val pnut: PnutServiceOption
    private val pnutRepository = PnutRepository(context)
    var pnutService: PnutService = pnutRepository.pnutService


    private val pageSize = 20
    private val pagingConfig = PagedList.Config.Builder().setInitialLoadSizeHint(pageSize).setPageSize(pageSize).build()

    sealed class State {
        object None : State()
        object Pending : State()
        object Resolve : State()
        data class Reject(val t: Throwable?) : State()
    }

    val state = MutableLiveData<State>().apply { postValue(State.None) }

    init {
//        val token = prefManager.getDefaultAccountToken()
//        val pnutService = PnutRequest(context, token)
//            .createService(PnutService::class.java)
        pnut = PnutServiceOption(pnutService, 20)
    }


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

    protected fun modMax(params: ItemKeyedDataSource.LoadParams<String>) =
        pnut.defaultPaging.copy(count = params.requestedLoadSize, maxId = params.key).toMap()

    protected fun modMin(params: ItemKeyedDataSource.LoadParams<String>) =
        pnut.defaultPaging.copy(count = params.requestedLoadSize, minId = params.key).toMap()

    protected fun error() = Throwable("You must set extra option")

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Post>) {
        // TODO: error handling
        LogUtil.d("loadInitial")
//        callback.onResult(arrayListOf())
        val paging = pnut.defaultPaging
//        val paging = Pageable(518819.toString(), null, 10)
        call(paging.toMap()).toList {
            val prev = if (it.isNotEmpty()) it.first().id else null
            val next = if (it.isNotEmpty()) it.last().id else null
            callback.onResult(it)
//            callback.onResult(it, 0, it.size)
        }
    }

    // load old posts
    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Post>) {
        LogUtil.d("loadAfter")
        LogUtil.e("loadAfter ${params.key}")
        call(modMin((params))).toList {
            val next = if (it.isNotEmpty()) it.last().id else null
//            callback.onResult(it, next)
            callback.onResult(it)
        }
    }

    // load new posts
    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Post>) {
        LogUtil.d("loadBefore ${params.key}")
        call(modMax(params)).toList {
            val prev = if (it.isNotEmpty()) it.first().id else null
//            callback.onResult(it, prev)
            LogUtil.e("it: ${it.size}")
            callback.onResult(it)
        }
    }

    private fun call(paging: Map<String, String>): Call<PnutResponse<List<Post>>> {
        return when (streamType) {
            PnutRepository.StreamType.Home -> pnut.service.getPersonalStream(paging)
            PnutRepository.StreamType.Mention -> pnut.service.getMentions(paging)
            PnutRepository.StreamType.Star -> pnut.service.getStars(paging)
            PnutRepository.StreamType.Conversations -> pnut.service.getConversations(paging)
            PnutRepository.StreamType.MissedConversations -> pnut.service.getMissedConversations(paging)
            PnutRepository.StreamType.Newcomers -> pnut.service.getNewcomers(paging)
            PnutRepository.StreamType.Photos -> pnut.service.getPhotos(paging)
            PnutRepository.StreamType.Trending -> pnut.service.getTrending(paging)
            PnutRepository.StreamType.Global -> pnut.service.getGlobal(paging)
            PnutRepository.StreamType.User -> pnut.service.getUserPosts(extra ?: throw error(), paging)
            PnutRepository.StreamType.Tag -> pnut.service.getTaggedPosts(extra ?: throw error(), paging)
        }
    }
}