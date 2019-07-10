package net.unsweets.gamma.presentation.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.LoginUtil
import net.unsweets.gamma.util.showAsError

class LoginActivity : AppCompatActivity() {
    enum class IntentKey {
        Error
    }
    companion object {
        fun getRetryIntent(context: Context, message: String) = Intent(context, LoginActivity::class.java).apply {
            putExtra(IntentKey.Error.name, message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton.setOnClickListener { launchLoginBrowserActivity() }
        signUpButton.setOnClickListener { launchSignUpBrowserActivity() }
        showSnackBarWhenRaisedError()
    }

    private fun showSnackBarWhenRaisedError() {
        val errorMessage = intent.getStringExtra(IntentKey.Error.name)
        if (errorMessage.isNullOrEmpty()) return
        Snackbar
            .make(findViewById<View>(android.R.id.content), errorMessage, Snackbar.LENGTH_LONG)
            .showAsError()
    }


    private fun launchLoginBrowserActivity() {
        val intent = LoginUtil.getLoginIntent(this)
        startActivity(intent)
        finish()
    }

    private fun launchSignUpBrowserActivity() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.sign_up_url)))
        startActivity(intent)
    }

}
