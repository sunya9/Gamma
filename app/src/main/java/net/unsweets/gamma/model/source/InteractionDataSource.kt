package net.unsweets.gamma.model.source

import android.content.Context
import net.unsweets.gamma.model.entity.Interaction
import net.unsweets.gamma.model.entity.PnutResponse
import retrofit2.Call

class InteractionDataSource(
    context: Context
) : BaseKeyedDataSource<Interaction>(context) {
    override fun getKey(item: Interaction): String = item.paginationId

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Interaction>) {

        // TODO: error handling
        call(pnut.defaultPaging.toMap()).toList {
            val prev = if (it.isNotEmpty()) it.first().paginationId else null
            val next = if (it.isNotEmpty()) it.last().paginationId else null
//            callback.onResult(it, prev, next)
            callback.onResult(it)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Interaction>) {
        call(modMin(params)).toList {
            val next = if (it.isNotEmpty()) it.last().paginationId else null
//            callback.onResult(it, next)
            callback.onResult(it)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Interaction>) {
        call(modMax(params)).toList {
            val prev = if (it.isNotEmpty()) it.first().paginationId else null
//            callback.onResult(it, prev)
            callback.onResult(it)
        }
    }

    private fun call(paging: Map<String, String>): Call<PnutResponse<List<Interaction>>> {
        return pnut.service.getInteractions(paging)
    }


}