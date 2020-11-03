package net.unsweets.gamma.di

import android.content.Context
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.presentation.activity.ComposePostActivity
import net.unsweets.gamma.presentation.activity.EntryActivity
import net.unsweets.gamma.presentation.activity.MainActivity
import net.unsweets.gamma.presentation.activity.VerifyTokenActivity
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    UseCaseModule::class,
    ServiceIntentModule::class
])
@Singleton
interface AppComponent : AndroidInjector<GammaApplication>  {
    fun inject(activity: MainActivity)
    fun inject(activity: EntryActivity)
    fun inject(activity: VerifyTokenActivity)
    fun inject(activity: ComposePostActivity)
    fun inject(context: Context)
    fun useCaseComponentBuilder(): UseCaseComponent.Builder
    fun serviceIntentComponentBuilder(): ServiceIntentComponent.Builder
//    fun inject(baseFragment: BaseFragment)
}