package net.unsweets.gamma.di

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import net.unsweets.gamma.GammaApplication
import net.unsweets.gamma.presentation.activity.EntryActivity
import net.unsweets.gamma.presentation.activity.VerifyTokenActivity
import net.unsweets.gamma.presentation.activity.MainActivity
import net.unsweets.gamma.presentation.fragment.BaseFragment
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    UseCaseModule::class
])
@Singleton
interface AppComponent : AndroidInjector<GammaApplication>  {
    fun inject(activity: MainActivity)
    fun inject(activity: EntryActivity)
    fun inject(activity: VerifyTokenActivity)
    fun inject(context: Context)
    fun useCaseComponentBuilder(): UseCaseComponent.Builder
//    fun inject(baseFragment: BaseFragment)
}