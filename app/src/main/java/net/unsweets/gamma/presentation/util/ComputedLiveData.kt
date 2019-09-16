package net.unsweets.gamma.presentation.util

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class ComputedLiveData {
    companion object {
        @MainThread
        fun <A, B, C> of(
            p0: LiveData<A>,
            p1: LiveData<B>,
            mapFunction: Function2<A?, B?, C>
        ): LiveData<C> {
            val result = MediatorLiveData<C>()
            result.addSource(p0) { x -> result.value = mapFunction(x, p1.value) }
            result.addSource(p1) { x -> result.value = mapFunction(p0.value, x) }
            return result
        }

        @MainThread
        fun <A, B, C, D> of(
            p0: LiveData<A>,
            p1: LiveData<B>,
            p2: LiveData<C>,
            mapFunction: Function3<A?, B?, C?, D>
        ): LiveData<D> {
            val result = MediatorLiveData<D>()
            result.addSource(p0) { x -> result.value = mapFunction(x, p1.value, p2.value) }
            result.addSource(p1) { x -> result.value = mapFunction(p0.value, x, p2.value) }
            result.addSource(p2) { x -> result.value = mapFunction(p0.value, p1.value, x) }
            return result
        }
    }
}