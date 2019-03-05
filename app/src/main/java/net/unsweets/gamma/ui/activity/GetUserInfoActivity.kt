package net.unsweets.gamma.ui.activity

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import net.unsweets.gamma.R
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.ui.base.BaseViewModel

class GetUserInfoActivity : BaseActivity() {

    private lateinit var viewModel: GetUserInfoViewModel
    private val eventObserver = Observer<Event> {
        when (it) {
            Event.Success -> success()
            Event.Failed -> failed()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_info)

        viewModel = ViewModelProviders.of(this).get(GetUserInfoViewModel::class.java)
        viewModel.event.observe(this, eventObserver)
        intent?.let {
            val uri = it.data ?: return failed()
            viewModel.storeTokenAndId(uri)
        }
    }

    enum class Event { Success, Failed }

    class GetUserInfoViewModel(app: Application) : BaseViewModel<Event>(app) {
        val user = MutableLiveData<User>()
        val token = MutableLiveData<String>()

        fun storeTokenAndId(uri: Uri) {
            // access_token=<token>
            val fragment = uri.fragment ?: return
            val token = fragment.split("=")[1]
            this.token.value = token
//            getPnutWithToken(token).service.token().enqueue(object : Callback<PnutResponse<Token>> {
//                override fun onFailure(call: Call<PnutResponse<Token>>, t: Throwable) {
//                    sendEvent(Event.Failed)
//                }
//
//                override fun onResponse(call: Call<PnutResponse<Token>>, response: Response<PnutResponse<Token>>) {
//                    val user = response.body()?.data?.user ?: return onFailure(call, Throwable("No user data"))
//                    this@GetUserInfoViewModel.user.value = user
//                    sendEvent(Event.Success)
//
//                }
//            })
        }
    }

    private fun success() {
        val user = viewModel.user.value ?: return
        val token = viewModel.token.value ?: return
        prefManager.setDefaultAccount(user.id, token)
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }


    private fun failed() {
        finish()
        val intent = LoginActivity.getRetryIntent(this)
        startActivity(intent)
    }
}

