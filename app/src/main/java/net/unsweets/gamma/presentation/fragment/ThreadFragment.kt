package net.unsweets.gamma.presentation.fragment


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_explore.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.StreamType

class ThreadFragment : PostItemFragment() {
    private enum class BundleKey { Post }

    private val post by lazy {
        arguments?.getParcelable<Post>(BundleKey.Post.name) ?: throw NullPointerException("You must set Post")
    }
    override val streamType: StreamType by lazy {
        StreamType.Thread(post.id)
    }

    override val reverse = true

    override fun getFragmentLayout(): Int = R.layout.fragment_explore
    override fun getRecyclerView(view: View): RecyclerView = view.exploreList
    override fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout = view.swipeRefreshLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.toolbar.setNavigationOnClickListener {
            backToPrevFragment()
        }
        view.toolbar.setTitle(R.string.thread)
        getRecyclerView(view).let {
            it.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, true).apply {
                stackFromEnd = true
            }
        }
    }

    companion object {
        fun newInstance(post: Post) = ThreadFragment().apply {
            arguments = Bundle().apply {
                putParcelable(BundleKey.Post.name, post)
            }
        }
    }
}
