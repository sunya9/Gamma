package net.unsweets.gamma.presentation.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.fragment.PostItemFragment

class ProfilePagerAdapter(val context: Context, fm: FragmentManager, userId: String) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private data class FragmentInfo(val fragment: Fragment, @StringRes val titleRes: Int)
    private val fragments = arrayOf(
        FragmentInfo(PostItemFragment.getUserPostInstance(userId), R.string.posts),
        FragmentInfo(PostItemFragment.getStarInstance(userId), R.string.favorites)
    )
    override fun getItem(position: Int): Fragment = fragments[position].fragment

    override fun getCount(): Int  = fragments.size
    override fun getPageTitle(position: Int): CharSequence? = context.getString(fragments[position].titleRes)
}