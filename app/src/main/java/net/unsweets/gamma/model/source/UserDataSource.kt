package net.unsweets.gamma.model.source

import android.content.Context
import net.unsweets.gamma.model.entity.PnutResponse
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.model.net.PnutRepository
import retrofit2.Call

class UserDataSource(
    context: Context,
    val id: String,
    val mode: PnutRepository.UserListMode
) : BaseKeyedDataSource<User>(context) {
    override fun getKey(item: User): String = item.id

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<User>) {

        // TODO: error handling
        call(pnut.defaultPaging.toMap()).toList {
            val prev = if (it.isNotEmpty()) it.first().id else null
            val next = if (it.isNotEmpty()) it.last().id else null
//            callback.onResult(it, prev, next)
            callback.onResult(it)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<User>) {
        call(modMin(params)).toList {
            val next = if (it.isNotEmpty()) it.last().id else null
//            callback.onResult(it, next)
            callback.onResult(it)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<User>) {
        call(modMax(params)).toList {
            val prev = if (it.isNotEmpty()) it.first().id else null
//            callback.onResult(it, prev)
            callback.onResult(it)
        }
    }

    private fun call(paging: Map<String, String>): Call<PnutResponse<List<User>>> {
        return when (mode) {
            PnutRepository.UserListMode.Followers -> pnut.service.getFollowers(id, paging)
            PnutRepository.UserListMode.Following -> pnut.service.getFollowing(id, paging)
        }
    }

}