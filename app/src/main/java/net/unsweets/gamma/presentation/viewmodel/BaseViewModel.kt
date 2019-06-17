package net.unsweets.gamma.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.util.SingleLiveEvent
import javax.inject.Inject

open class BaseViewModel<T> @Inject constructor(app: Application): AndroidViewModel(app) {
//    init {
//        DaggerAppComponent.builder().appModule(AppModule(app)).build().also {
//            it.inject(PreferenceRepository(app))
//            it.inject(PnutRepository(app))
//        }
//    }
    protected val context = app
    @Inject
    lateinit var pnutRepository: IPnutRepository
//    protected val pnut = pnutRepository.pnutService
//    protected fun getPnutWithToken(token: String) = PnutRepository().pnutServiceOption
    private val _event = SingleLiveEvent<T>()


    val event: LiveData<T> = _event

    fun sendEvent(sendEvent: T) {
        _event.value = sendEvent
        _event.call()
    }
    interface Refreshable {
        fun onRefreshed()
    }
}