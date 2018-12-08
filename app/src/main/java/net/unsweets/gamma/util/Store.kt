package net.unsweets.gamma.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

open class Store<T>: ViewModel() {
    private val _event = SingleLiveEvent<T>()
    val event = Transformations.map(_event) { it }

    fun sendEvent(sendEvent: T) {
        _event.value = sendEvent
        _event.call()
    }
}