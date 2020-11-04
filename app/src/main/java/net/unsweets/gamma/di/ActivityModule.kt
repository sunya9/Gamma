package net.unsweets.gamma.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.unsweets.gamma.presentation.activity.*

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeEntryActivity(): EntryActivity

    @ContributesAndroidInjector
    abstract fun contributeVerifyTokenActivity(): VerifyTokenActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeBaseActivity(): BaseActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeFilesActivity(): FilesActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributePhotoViewActivity(): PhotoViewActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeEditPhotoActivity(): EditPhotoActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeShareActivity(): ShareActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeComposePostActivity(): ComposePostActivity

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributeEditProfileActivity(): EditProfileActivity
}