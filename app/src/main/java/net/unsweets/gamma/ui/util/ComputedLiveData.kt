package net.unsweets.gamma.ui.util

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class ComputedLiveData {
    companion object {
        @MainThread
        fun <A, B, C> of(p0: LiveData<A>, p1: LiveData<B>, mapFunction: Function2<A?, B?, C>): LiveData<C> {
            val result = MediatorLiveData<C>()
            result.addSource(p0) { x -> result.value = mapFunction(x, p1.value) }
            result.addSource(p1) { x -> result.value = mapFunction(p0.value, x) }
            return result
        }
    }
}