package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.list_with_toolbar.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.StreamType

class TagStreamFragment : PostItemFragment() {
    private val hashTag: String by lazy { arguments?.getString(BundleKey.Tag.name, "") ?: "" }
    override val streamType: StreamType by lazy {
        StreamType.Tag(hashTag)
    }

    override fun getFragmentLayout(): Int = R.layout.list_with_toolbar
    override fun getRecyclerView(view: View): RecyclerView = view.itemList
    override fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout = view.swipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.toolbar.title = getString(R.string.tag_stream_fragment_title_template, hashTag)
        view.toolbar.setNavigationOnClickListener {
            backToPrevFragment()
        }
    }
    private enum class BundleKey { Tag }

    companion object {
        fun newInstance(tag: String) = TagStreamFragment().apply {
            arguments = Bundle().apply {
                putString(BundleKey.Tag.name, tag)
            }
        }
    }

}