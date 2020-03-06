package net.unsweets.gamma.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import net.unsweets.gamma.domain.repository.*
import javax.inject.Singleton

@Module(
  subcomponents = [
//    FakeUseCaseComponent::class
  ]
)
class FakeAppModule(private val application: Application) {
  private val accountRepository = AccountRepository(application)
  private val pnutRepository =
    PnutRepository(application, accountRepository.getDefaultAccount()?.token)
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
  fun providePnutCacheRepository(): IPnutCacheRepository =
    PnutCacheRepository(accountRepository.getDefaultAccount()?.id, application)

  @Provides
  @Singleton
  fun provideAccountRepository(): IAccountRepository = accountRepository
}