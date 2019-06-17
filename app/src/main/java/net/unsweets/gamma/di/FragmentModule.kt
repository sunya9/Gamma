package net.unsweets.gamma.di

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.unsweets.gamma.presentation.fragment.*

@Module
abstract class FragmentModule {
    // home
    @ContributesAndroidInjector
    abstract fun contributeHomeStream(): PostItemFragment.HomeStream
    @ContributesAndroidInjector
    abstract fun contributeMentionsStream(): PostItemFragment.MentionsStream
    @ContributesAndroidInjector
    abstract fun contributeInteractionStream(): InteractionFragment
    @ContributesAndroidInjector
    abstract fun contributeStarsStream(): SpecificUserPostFragment.StarsPostFragment

    // compose post fragments
    @ContributesAndroidInjector
    abstract fun contributeComposePostFragment(): ComposePostFragment

    // profile fragments
    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment
    @ContributesAndroidInjector
    abstract fun contributeUserPostFragment(): SpecificUserPostFragment.UserPostFragment
    @ContributesAndroidInjector
    abstract fun contributeFollowerListFragment(): UserListFragment.FollowerListFragment
    @ContributesAndroidInjector
    abstract fun contributeFollowingListFragment(): UserListFragment.FollowingListFragment

    // other streams
    @ContributesAndroidInjector
    abstract fun contributeConversationsFragment(): ExploreFragment.ConversationsFragment
    @ContributesAndroidInjector
    abstract fun contributeMissedConversationsFragment(): ExploreFragment.MissedConversationsFragment
    @ContributesAndroidInjector
    abstract fun contributeNewComersFragment(): ExploreFragment.NewcomersFragment
    @ContributesAndroidInjector
    abstract fun contributePhotosFragment(): ExploreFragment.PhotosFragment
    @ContributesAndroidInjector
    abstract fun contributeTrendingFragment(): ExploreFragment.TrendingFragment
    @ContributesAndroidInjector
    abstract fun contributeGlobalFragment(): ExploreFragment.GlobalFragment
    @ContributesAndroidInjector
    abstract fun contributeFileListFragment(): FileListFragment
    @ContributesAndroidInjector
    abstract fun contributeTagStreamFragment(): TagStreamFragment

}
