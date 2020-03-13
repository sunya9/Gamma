package net.unsweets.gamma.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import net.unsweets.gamma.util.SingleLiveEvent

open class EventViewModel<T>: ViewModel() {
    private val _event = SingleLiveEvent<T>()
    val event: LiveData<T> = _event

    fun sendEvent(sendEvent: T) {
        _event.value = sendEvent
    }

}