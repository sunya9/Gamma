package net.unsweets.gamma.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.unsweets.gamma.service.PostService

@Module
abstract class ServiceIntentModule {
    @ContributesAndroidInjector
    abstract fun contributePostService(): PostService
}