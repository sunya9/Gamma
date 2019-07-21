package net.unsweets.gamma.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.unsweets.gamma.util.SingleLiveEvent
import javax.inject.Inject

open class BaseViewModel<T> @Inject constructor(app: Application): AndroidViewModel(app) {
    private val _event = SingleLiveEvent<T>()
    val event: LiveData<T> = _event
}