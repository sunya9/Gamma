package net.unsweets.gamma.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import net.unsweets.gamma.util.PrefManager


class EntryActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultAccountToken = PrefManager(applicationContext).getDefaultAccountToken()
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
