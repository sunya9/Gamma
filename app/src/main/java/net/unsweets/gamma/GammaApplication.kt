package net.unsweets.gamma

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.di.AppModule
import net.unsweets.gamma.di.DaggerAppComponent
import net.unsweets.gamma.domain.usecases.SetupTokenUseCase
import net.unsweets.gamma.presentation.activity.LoginActivity
import net.unsweets.gamma.presentation.util.ThemeColorUtil


class GammaApplication : DaggerApplication(), CoroutineScope by MainScope() {
    val module by lazy { AppModule(this) }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {

        return DaggerAppComponent.builder().appModule(module).build()
    }

    override fun onCreate() {
        updateBaseTheme()
        updateTheme()
        super.onCreate()
        val config = BundledEmojiCompatConfig(this)
            .setReplaceAll(true)
        EmojiCompat.init(config)
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())
//        if (!setToken()) return backToLoginActivity() // failed

    }

    fun updateBaseTheme() {
        val darkMode = module.providePreferenceRepository().darkMode
        AppCompatDelegate.setDefaultNightMode(darkMode.value)
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
                module.providePnutRepository(),
                module.provideAccountRepository()
            ).run(Unit)
        }.existDefaultAccount
    }

    fun updateTheme() {
        ThemeColorUtil.applyTheme(this)
    }

    companion object {
        fun getInstance(activity: Activity) = activity.application as GammaApplication
    }
}
