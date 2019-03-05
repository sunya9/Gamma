package net.unsweets.gamma.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import net.unsweets.gamma.ui.util.PrefManager


class EntryActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefManager = PrefManager(this)
        val defaultAccountToken = prefManager.getDefaultAccountToken()
//        findViewById<View>(android.R.id.content).systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val intentClass: Class<out Activity> =
            if (defaultAccountToken != null)
                MainActivity::class.java
            else
                LoginActivity::class.java

        val intent = Intent(applicationContext, intentClass)
        startActivity(intent)
        finish()
    }
}
