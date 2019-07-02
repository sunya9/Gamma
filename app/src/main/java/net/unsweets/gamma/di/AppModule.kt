package net.unsweets.gamma.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import net.unsweets.gamma.domain.repository.*
import javax.inject.Singleton

@Module(
    subcomponents = [
        UseCaseComponent::class
    ]
)
class AppModule(private val application: Application) {
    //    private val database = Room.databaseBuilder(application, Database::class.java, "gamma.db").build()
    private val accountRepository = AccountRepository(application)
    private val pnutRepository = PnutRepository(application)
    private val preferenceRepository = PreferenceRepository(application)

    @Provides
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun providePreferenceRepository(): IPreferenceRepository = preferenceRepository

    @Provides
    @Singleton
    fun providePnutRepository(): IPnutRepository = pnutRepository

    @Provides
    @Singleton
    fun provideAccountRepository(): IAccountRepository = accountRepository
}