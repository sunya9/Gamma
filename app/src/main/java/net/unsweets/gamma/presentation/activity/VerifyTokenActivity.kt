package net.unsweets.gamma.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.io.VerifyTokenInputData
import net.unsweets.gamma.domain.usecases.VerifyTokenUseCase
import net.unsweets.gamma.presentation.viewmodel.EventViewModel
import javax.inject.Inject

class VerifyTokenActivity : BaseActivity() {
    @Inject
    lateinit var verifyTokenUseCase: VerifyTokenUseCase
    private val viewModel: GetUserInfoViewModel by lazy {
        ViewModelProviders
            .of(this, GetUserInfoViewModel.Factory(verifyTokenUseCase))
            .get(GetUserInfoViewModel::class.java)
    }
    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.Success -> success()
            is Event.Failure -> failure(it.t)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_get_user_info)
        viewModel.event.observe(this, eventObserver)
        verifyToken()
    }

    private fun verifyToken() {
        val uri = intent.data ?: return failure(Throwable(getString(R.string.cannot_get_intent_data)))
        viewModel.verifyToken(uri)
    }

    sealed class Event {
        object Success: Event()
        data class Failure(val t: Throwable): Event()
    }

    class GetUserInfoViewModel(
        private val verifyTokenUseCase: VerifyTokenUseCase): EventViewModel<Event>() {
        fun verifyToken(uri: Uri) {
            // access_token=<token>
            val fragment = uri.fragment ?: return
            val token = fragment.split("=")[1]
            viewModelScope.launch {
                runCatching {
                    verifyTokenUseCase.run(VerifyTokenInputData(token))
                }.onSuccess {
                    sendEvent(Event.Success)
                }.onFailure {
                    sendEvent(Event.Failure(it))
                }
            }
        }

        class Factory(private val verifyTokenUseCase: VerifyTokenUseCase) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GetUserInfoViewModel(verifyTokenUseCase) as T
            }

        }
    }

    private fun success() {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }


    private fun failure(t: Throwable) {
        finish()
        val intent = LoginActivity.getRetryIntent(this, t.localizedMessage)
        startActivity(intent)
    }
}
