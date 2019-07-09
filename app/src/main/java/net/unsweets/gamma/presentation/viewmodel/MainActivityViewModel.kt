package net.unsweets.gamma.presentation.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.usecases.GetAuthenticatedUserUseCase
import net.unsweets.gamma.presentation.activity.MainActivity

class MainActivityViewModel(private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase) : EventViewModel<MainActivity.Event>() {
    private val token = MutableLiveData<Token>()
    val user = Transformations.map(token) { it?.user }
    val showAccountMenu = MutableLiveData<Boolean>().apply { value = false }
    init {
        getUserInfo()
    }
    private fun getUserInfo() {
        viewModelScope.launch {
            runCatching {
                getAuthenticatedUserUseCase.run(Unit)
            }.onSuccess {
                token.value = it.token
            }
        }
    }

    fun openMyProfile() {
        val user = user.value ?: return
        sendEvent(MainActivity.Event.OpenMyProfile(user))
    }
    fun composePost() {
        sendEvent(MainActivity.Event.ComposePost)
    }

    fun toggleNavigationViewMenu() {
        showAccountMenu.value = !(showAccountMenu.value ?: false)
    }
    class Factory(private val getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(getAuthenticatedUserUseCase) as T
        }

    }
}