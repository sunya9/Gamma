package net.unsweets.gamma.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.domain.usecases.SetupTokenUseCase
import javax.inject.Inject
import kotlin.reflect.KClass


class EntryActivity : BaseActivity(), CoroutineScope by MainScope() {
    @Inject
    lateinit var setupTokenUseCase: SetupTokenUseCase

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        findViewById<View>(android.R.id.content).systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        val existDefaultAccount: Boolean = runBlocking {
            val res = setupTokenUseCase.run(Unit)
            res.existDefaultAccount
        }
        val intentClass: KClass<out Activity> =
            if (existDefaultAccount)
                MainActivity::class
            else
                LoginActivity::class

        val intent = Intent(this, intentClass.java)
        startActivity(intent)
        finish()
    }
}
