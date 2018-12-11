package net.unsweets.gamma.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import net.unsweets.gamma.R
import net.unsweets.gamma.model.Token

class LoginActivity : AppCompatActivity() {
    enum class IntentKey {
        ERROR
    }
    companion object {
        fun getRetryIntent(context: Context): Intent {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra(IntentKey.ERROR.name, true)
            return intent
        }
    }
    private val scopes = arrayOf(
        Token.Scope.BASIC,
        Token.Scope.STREAM,
        Token.Scope.WRITE_POST,
        Token.Scope.FOLLOW,
        Token.Scope.UPDATE_PROFILE,
        Token.Scope.PRESENCE,
        Token.Scope.MESSAGES,
        Token.Scope.FILES,
        Token.Scope.POLLS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton.setOnClickListener { launchLoginBrowserActivity() }
        signUpButton.setOnClickListener { launchSignUpBrowserActivity() }
        intent?.let {
            val hasError = it.getBooleanExtra(IntentKey.ERROR.name, false)
            if(!hasError) return
            showErrorSnack()
        }
    }

    private fun showErrorSnack() {

    }


    private fun launchLoginBrowserActivity() {
        val url = createLoginURL()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
        finish()
    }

    private fun launchSignUpBrowserActivity() {
        Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.sign_up_url)))
        startActivity(intent)
    }

    private fun createLoginURL(): String {
        val clientId = getString(R.string.client_id)
        val scopeStr = scopes.joinToString(",")
        return getString(R.string.authenticate_url, clientId, scopeStr)
    }
}
