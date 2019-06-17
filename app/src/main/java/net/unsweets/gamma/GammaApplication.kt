package net.unsweets.gamma

import android.app.Activity
import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.DaggerAppCompatActivity_MembersInjector
import dagger.android.support.DaggerApplication
import net.unsweets.gamma.di.AppComponent
import net.unsweets.gamma.di.AppModule
import net.unsweets.gamma.di.DaggerAppComponent
import net.unsweets.gamma.di.UseCaseModule
import net.unsweets.gamma.domain.repository.PnutRepository
import net.unsweets.gamma.domain.repository.PreferenceRepository
import javax.inject.Inject

class GammaApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val module = AppModule(this)
        return DaggerAppComponent.builder().appModule(module).build()
    }

    override fun onCreate() {
        super.onCreate()
        val config = BundledEmojiCompatConfig(this)
            .setReplaceAll(true)
        EmojiCompat.init(config)


    }
}
