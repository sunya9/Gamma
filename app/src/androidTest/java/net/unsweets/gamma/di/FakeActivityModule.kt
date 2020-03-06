package net.unsweets.gamma.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.unsweets.gamma.presentation.activity.EntryActivityTest

@Module
abstract class FakeActivityModule {
  @ContributesAndroidInjector
  abstract fun contributeEntryActivityTest(): EntryActivityTest

}