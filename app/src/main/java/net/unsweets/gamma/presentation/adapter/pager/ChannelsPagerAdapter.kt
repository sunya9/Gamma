package net.unsweets.gamma.presentation.adapter.pager

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.fragment.ChannelListFragment

class ChannelsPagerAdapter(
    private val context: Context,
    fm: FragmentManager
) :
    FragmentPagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    data class FragmentInfo(val fragment: Fragment, @StringRes val titleRes: Int)

    private val fragments = listOf(
        FragmentInfo(
            ChannelListFragment.privateChannels(),
            R.string.private_channels
        ),
        FragmentInfo(
            ChannelListFragment.publicChannels(),
            R.string.public_channels
        )
    )

    override fun getItem(position: Int): Fragment = fragments[position].fragment

    override fun getPageTitle(position: Int): CharSequence? =
        context.getString(fragments[position].titleRes)

    override fun getCount(): Int = fragments.size
}