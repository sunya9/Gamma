package net.unsweets.gamma.di

import android.content.Context
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.presentation.activity.*
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
    fun inject(activity: EditProfileActivity)
    fun inject(context: Context)
    fun useCaseComponentBuilder(): UseCaseComponent.Builder
    fun serviceIntentComponentBuilder(): ServiceIntentComponent.Builder
//    fun inject(baseFragment: BaseFragment)
}