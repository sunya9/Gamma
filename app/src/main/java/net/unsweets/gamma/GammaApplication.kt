package net.unsweets.gamma

import android.content.Intent
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.di.AppModule
import net.unsweets.gamma.di.DaggerAppComponent
import net.unsweets.gamma.domain.usecases.SetupTokenUseCase
import net.unsweets.gamma.presentation.activity.LoginActivity

class GammaApplication : DaggerApplication(), CoroutineScope by MainScope() {
    val module by lazy { AppModule(this) }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        return DaggerAppComponent.builder().appModule(module).build()
    }

    override fun onCreate() {
        super.onCreate()
        val config = BundledEmojiCompatConfig(this)
            .setReplaceAll(true)
        EmojiCompat.init(config)
        if (!setToken()) return backToLoginActivity() // failed
    }

    private fun backToLoginActivity() {
        val newIntent = Intent(applicationContext, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(newIntent)
    }

    private fun setToken(): Boolean {
        return runBlocking {
            SetupTokenUseCase(
                module.provideProvidePnutServiceService(),
                module.providePreferenceRepository()
            ).run(Unit)
        }.existDefaultAccount
    }
}
