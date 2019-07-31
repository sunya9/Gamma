package net.unsweets.gamma.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.unsweets.gamma.presentation.activity.SettingsActivity
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
    abstract fun contributeComposePostDialogFragment(): ComposePostDialogFragment
    // compose post fragments
    @ContributesAndroidInjector
    abstract fun contributeComposePostFragment(): ComposePostFragment
    // profile fragments
    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeEditProfileFragment(): EditProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeUserPostFragment(): SpecificUserPostFragment.UserPostFragment

    @ContributesAndroidInjector
    abstract fun contributeFollowerListFragment(): FollowingFollowerListFragment.FollowerListFragment

    @ContributesAndroidInjector
    abstract fun contributeFollowingListFragment(): FollowingFollowerListFragment.FollowingListFragment

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

    @ContributesAndroidInjector
    abstract fun contributeThreadFragment(): ThreadFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsActivity.SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountPreferenceFragment(): SettingsActivity.AccountPreferenceFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchPostsFragment(): PostItemFragment.SearchPostsFragment

    @ContributesAndroidInjector
    abstract fun conrtirbuteSearchUserListFragment(): UserListFragment.SearchUserListFragment
}
