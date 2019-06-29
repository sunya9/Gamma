package net.unsweets.gamma.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import net.unsweets.gamma.data.db.Database
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.domain.repository.PnutRepository
import net.unsweets.gamma.domain.repository.PreferenceRepository
import javax.inject.Singleton

@Module(subcomponents = [
    UseCaseComponent::class
])
class AppModule(private val application: Application) {
    @Provides
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun providePreferenceRepository(): IPreferenceRepository = PreferenceRepository(application)

    @Provides
    @Singleton
    fun providePnutRepository(): IPnutRepository = PnutRepository(application)

    @Provides
    @Singleton
    fun provideDatabase() = Room.databaseBuilder(application, Database::class.java, "gamma.db").build()
}