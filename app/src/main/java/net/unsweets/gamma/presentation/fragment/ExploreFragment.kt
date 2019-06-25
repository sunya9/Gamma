package net.unsweets.gamma.presentation.fragment


import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_explore.view.*
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.presentation.util.DrawerContentFragment
import net.unsweets.gamma.presentation.util.FragmentHelper
import net.unsweets.gamma.presentation.util.SmoothScroller


sealed class ExploreFragment : PostItemFragment(), DrawerContentFragment {

    override fun getFragmentLayout(): Int = net.unsweets.gamma.R.layout.fragment_explore
    override fun getRecyclerView(view: View): RecyclerView = view.exploreList

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view.toolbar)
    }

    private fun setupToolbar(toolbar: Toolbar) {
        toolbar.setNavigationOnClickListener { FragmentHelper.backFragment(fragmentManager) }
        toolbar.setOnClickListener {
            val ctx = context ?: return@setOnClickListener
            getRecyclerView(view!!).layoutManager?.startSmoothScroll(SmoothScroller(ctx))
        }
        setTitleToToolbar(toolbar)

    }

    private fun setTitleToToolbar(toolbar: Toolbar) {
        (streamType as? StreamType.Explore)?.let {
            toolbar.setTitle(it.titleRes)
        }
    }

    class ConversationsFragment : ExploreFragment() {
        override val streamType = StreamType.Explore.Conversations
        override val menuItemId = net.unsweets.gamma.R.id.conversations

        companion object {
            fun newInstance() = ConversationsFragment()
        }
    }

    class MissedConversationsFragment : ExploreFragment() {
        override val streamType = StreamType.Explore.MissedConversations
        override val menuItemId = net.unsweets.gamma.R.id.missedConversations

        companion object {
            fun newInstance() = MissedConversationsFragment()
        }
    }

    class NewcomersFragment : ExploreFragment() {
        override val streamType = StreamType.Explore.Newcomers
        override val menuItemId = net.unsweets.gamma.R.id.newcomers

        companion object {
            fun newInstance() = NewcomersFragment()
        }
    }

    class PhotosFragment : ExploreFragment() {
        override val streamType = StreamType.Explore.Photos
        override val menuItemId = net.unsweets.gamma.R.id.photos

        companion object {
            fun newInstance() = PhotosFragment()
        }
    }

    class TrendingFragment : ExploreFragment() {
        override val streamType = StreamType.Explore.Trending
        override val menuItemId = net.unsweets.gamma.R.id.trending

        companion object {
            fun newInstance() = TrendingFragment()
        }
    }

    class GlobalFragment : ExploreFragment() {
        override val streamType = StreamType.Explore.Global
        override val menuItemId = net.unsweets.gamma.R.id.global

        companion object {
            fun newInstance() = GlobalFragment()
        }
    }
}
