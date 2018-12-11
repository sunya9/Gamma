package net.unsweets.gamma.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import net.unsweets.gamma.R
import net.unsweets.gamma.api.PnutResponse
import net.unsweets.gamma.api.PnutService
import net.unsweets.gamma.model.Token
import net.unsweets.gamma.util.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetUserInfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_user_info)

        intent?.let {
            val uri = it.data ?: return failed()
            storeTokenAndId(uri)
        }
    }

    private fun storeTokenAndId(uri: Uri) {
        // access_token=<token>
        val fragment = uri.fragment ?: return
        val token = fragment.split("=")[1]
        PnutService.getService(token).token().enqueue(object : Callback<PnutResponse<Token>> {
            override fun onFailure(call: Call<PnutResponse<Token>>, t: Throwable) {
                failed()
            }

            override fun onResponse(call: Call<PnutResponse<Token>>, response: Response<PnutResponse<Token>>) {
                val user = response.body()?.data?.user ?: return failed()
                PrefManager(applicationContext).setDefaultAccount(user.id, token)
                val mainIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        })
    }

    private fun failed() {
        finish()
        val intent = LoginActivity.getRetryIntent(this)
        startActivity(intent)
    }
}

