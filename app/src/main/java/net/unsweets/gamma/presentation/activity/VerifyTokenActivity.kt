package net.unsweets.gamma.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.ErrorResponse
import net.unsweets.gamma.domain.model.io.VerifyTokenInputData
import net.unsweets.gamma.domain.usecases.VerifyTokenUseCase
import net.unsweets.gamma.presentation.viewmodel.EventViewModel
import net.unsweets.gamma.util.ErrorCollections
import javax.inject.Inject

class VerifyTokenActivity : BaseActivity() {
    @Inject
    lateinit var verifyTokenUseCase: VerifyTokenUseCase
    private val viewModel: GetUserInfoViewModel by lazy {
        ViewModelProvider(
            this,
            GetUserInfoViewModel.Factory(verifyTokenUseCase)
        )[GetUserInfoViewModel::class.java]
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
        val uri =
            intent.data ?: return failure(Exception(getString(R.string.cannot_get_intent_data)))
        // uri will receive gamma://authenticate#error_description=resource+owner+denied+your+app+access&error=access_denied
        val modUri = Uri.parse(uri.toString().replace("#", "?"))
        intent
        if (modUri.getQueryParameter("error") == "access_denied") {
            try {
                val res = modUri.getQueryParameter("error_description")?.let {
                    ErrorResponse.getResource(it)
                } ?: R.string.cannot_get_intent_data
                failure(Exception(getString(res)))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }

        } else {
            viewModel.verifyToken(uri)
        }

    }

    sealed class Event {
        object Success : Event()
        data class Failure(val t: Throwable) : Event()
    }

    class GetUserInfoViewModel(
        private val verifyTokenUseCase: VerifyTokenUseCase
    ) : EventViewModel<Event>() {
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

        class Factory(private val verifyTokenUseCase: VerifyTokenUseCase) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return GetUserInfoViewModel(verifyTokenUseCase) as T
            }

        }
    }

    private fun success() {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(mainIntent)
        finish()
    }


    private fun failure(t: Throwable) {
        finish()
        val message = when (t) {
            is ErrorCollections.CommunicationError -> t.errorResponse.meta.getResourceMessage(this)
            else -> t.message.orEmpty()
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//        val intent = LoginActivity.getRetryIntent(this, message)
//        startActivity(intent)
    }
}

