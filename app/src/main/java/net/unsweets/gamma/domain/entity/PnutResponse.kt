package net.unsweets.gamma.domain.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class PnutResponse<T>(val meta: Meta, val data: T) {
    data class Meta(
        val code: Int,
        val min_id: String?,
        val max_id: String?,
        val more: Boolean?
    )
    fun intoLiveData(liveData: MutableLiveData<PnutResponse<T>>) {
        liveData.value = this
    }
}
