package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_post_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.raw.OEmbed
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.GetPostInputData
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.usecases.GetPostUseCase
import net.unsweets.gamma.presentation.activity.PhotoViewActivity
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.adapter.ThumbnailViewPagerAdapter
import net.unsweets.gamma.presentation.util.*
import net.unsweets.gamma.presentation.util.DateUtil.Companion.getShortDateStr
import net.unsweets.gamma.service.PostService
import net.unsweets.gamma.util.LogUtil
import java.util.*
import javax.inject.Inject


abstract class PostItemFragment : BaseListFragment<Post, PostItemFragment.PostViewHolder.Exist>(),
    BaseListRecyclerViewAdapter.IBaseList<Post, PostItemFragment.PostViewHolder.Exist>,
    ThumbnailViewPagerAdapter.Listener, DeletePostDialogFragment.Callback {
    override fun ok(position: Int, post: Post) {
        PostService.newDeletePostIntent(context, post.id)
        adapter.removeItem(position)
    }

    override fun cancel() {}

    override fun onClick(path: String, position: Int, items: List<String>) {
        val newIntent = PhotoViewActivity.photoViewInstance(context!!, items, position)
        startActivity(newIntent)
    }

    override val itemNameRes: Int = R.string.posts

    private val itemTouchHelper: ItemTouchHelper by lazy {
        val postTouchHelperCallback = PostTouchHelperCallback(context!!, adapter)
        ItemTouchHelper(postTouchHelperCallback)
    }
    private val entityListener: View.OnTouchListener = EntityOnTouchListener()

    private lateinit var moveTransition: Transition

    override lateinit var viewModel: PostItemViewModel

    private val postsObserver = Observer<List<Post>> {

    }

    @Inject
    lateinit var getPostUseCase: GetPostUseCase
    abstract val streamType: StreamType
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, PostItemViewModel.Factory(streamType, getPostUseCase))
            .get(PostItemViewModel::class.java)
        super.onCreate(savedInstanceState)
        moveTransition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.image_shared_element_transition)
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        moveTransition.duration = duration
    }

    override val baseListListener by lazy { this }

    override fun createViewHolder(mView: View, viewType: Int): PostViewHolder.Exist =
        PostViewHolder.Exist(mView, itemTouchHelper)

    override fun onClickItemListener(item: Post) {
        val fragment = getThreadInstance(item)
        addFragment(fragment, item.id)
    }

    private enum class DialogKey { Compose, DeletePost }

    override fun onBindViewHolder(item: Post, viewHolder: PostViewHolder.Exist, position: Int) {
        val url = item.mainPost.user?.let {
            "${it.content.avatarImage.link}?w=96"
        } ?: "" //
        val context = viewHolder.itemView.context

        if (item.isDeleted == true) {
            viewHolder.itemView.alpha = 0.5f
            viewHolder.screenNameTextView.setText(R.string.deleted_post_user_name)
            viewHolder.handleNameTextView.text = ""
            viewHolder.bodyTextView.setText(R.string.this_post_has_deleted)
            viewHolder.avatarView.setImageResource(R.drawable.ic_delete_black_24dp)
        } else {
            viewHolder.itemView.alpha = 1f
            GlideApp.with(this).load(url).into(viewHolder.avatarView)
            val iconTransition = getString(R.string.icon_transition)
            val profileTransition = getString(R.string.profile_transition)
            val iconTransitionName = "$iconTransition + $position ${viewHolder.hashCode()}"
            viewHolder.avatarView.transitionName = iconTransitionName
            viewHolder.avatarView.setOnClickListener {
                LogUtil.e("it.transitionName: ${it.transitionName}")
                val transitionMap = HashMap<View, String>(
                    hashMapOf<View, String>(
                        Pair(it, it.transitionName)
//                    Pair(viewHolder.itemView, profileTransition)
                    )
                )
                val id = item.mainPost.user?.id ?: return@setOnClickListener
//            viewHolder.itemView.transitionName = profileTransition
                val fragment = ProfileFragment.newInstance(id, url, item.mainPost.user, it.transitionName)
                sharedElementReturnTransition = moveTransition
                sharedElementEnterTransition = moveTransition
                fragment.sharedElementEnterTransition = moveTransition
                fragment.sharedElementReturnTransition = moveTransition
                FragmentHelper.addFragment(context!!, fragment, id, transitionMap)
            }
            item.mainPost.user?.let {
                viewHolder.screenNameTextView.text = it.username
                viewHolder.handleNameTextView.text = it.name
            }
            viewHolder.bodyTextView.apply {
                text = item.mainPost.content?.getSpannableStringBuilder(context)
                setOnTouchListener(entityListener)
            }
            viewHolder.starTextView.let {
                it.setOnClickListener {
                    val newState = item.mainPost.youBookmarked == false
                    PostService.newStarIntent(context, item.mainPost.id, newState)
                    // TODO: revert state when raised error
                    // star "this post"
                    item.mainPost.youBookmarked = newState
                    adapter.notifyItemChanged(position)
                }
                val starTextRes = if (item.mainPost.youBookmarked == true) R.string.unstar else R.string.star
                val drawableRes =
                    if (item.mainPost.youBookmarked == true) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp
                it.text = context.getString(starTextRes)
                it.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes, 0, 0, 0)
            }
            viewHolder.replyTextView.setOnClickListener {
                val pos = getViewPositionOnScreen(viewHolder.avatarView)
                val fragment = ComposePostFragment.replyInstance(pos.first, pos.second, item.mainPost)
                fragment.show(childFragmentManager, DialogKey.Compose.name)

            }
            viewHolder.repostTextView.let {
                val repostType = when {
                    item.mainPost.youReposted == true -> RepostButtonType.DeleteRepost
                    item.mainPost.user?.me == true -> RepostButtonType.DeletePost
                    else -> RepostButtonType.Repost
                }
                it.setText(repostType.textRes)
                it.setCompoundDrawablesRelativeWithIntrinsicBounds(repostType.iconRes, 0, 0, 0)
                it.setOnClickListener {
                    when (repostType) {
                        RepostButtonType.DeletePost -> {
                            if (item.mainPost.user?.me == false) return@setOnClickListener
                            val dialog = DeletePostDialogFragment.newInstance(position, item.mainPost)
                            dialog.show(childFragmentManager, DialogKey.DeletePost.name)
                        }
                        RepostButtonType.DeleteRepost,
                        RepostButtonType.Repost -> {
                            val newState = item.mainPost.youReposted == false
                            PostService.newRepostIntent(context, item.mainPost.id, newState)
                            item.mainPost.youReposted = newState
                        }
                    }
                    // TODO: revert state when raised error
                    adapter.notifyItemChanged(position)
                }
            }
            setupRepostView(item, viewHolder.repostedByTextView)

        }
        viewHolder.starStateView.visibility = if (item.mainPost.youBookmarked == true) View.VISIBLE else View.GONE
        viewHolder.repostStateView.visibility = if (item.mainPost.youReposted == true) View.VISIBLE else View.GONE

        viewHolder.dateTextView.text = getShortDateStr(viewHolder.itemView.context, item.mainPost.createdAt)

        val raw = item.raw
        val photos = OEmbed.Photo.getPhotos(raw)
        if (photos.isNotEmpty()) {
            viewHolder.thumbnailViewPagerFrameLayout.visibility = View.VISIBLE
            viewHolder.thumbnailViewPager.adapter = ThumbnailViewPagerAdapter(photos, this)
            TabLayoutMediator(
                viewHolder.thumbnailTabLayout,
                viewHolder.thumbnailViewPager
            ) { _: TabLayout.Tab, _: Int ->
            }.attach()
            viewHolder.thumbnailTabLayout.visibility = if (photos.size == 1) View.GONE else View.VISIBLE
        } else {
            viewHolder.thumbnailViewPagerFrameLayout.visibility = View.GONE
            viewHolder.thumbnailViewPager.adapter = null
        }
    }

    private enum class RepostButtonType(@StringRes val textRes: Int, @DrawableRes val iconRes: Int) {
        DeleteRepost(R.string.delete_repost, R.drawable.ic_repeat_black_24dp),
        DeletePost(R.string.delete_post, R.drawable.ic_delete_black_24dp),
        Repost(R.string.repost, R.drawable.ic_repeat_border_black_24dp)
    }

    private fun setupRepostView(item: Post, repostedByTextView: TextView) {
        val originalUser = item.user
        if (item.repostOf != null && originalUser != null) {
            repostedByTextView.setOnClickListener {
                val fragment = ProfileFragment.newInstance(originalUser.id)
                addFragment(fragment, originalUser.id)
            }
            repostedByTextView.text =
                repostedByTextView.context.getString(R.string.reposted_by_template, originalUser.username)
            repostedByTextView.visibility = View.VISIBLE
        } else {
            repostedByTextView.visibility = View.GONE
        }
    }

    override fun getItemLayout(): Int = R.layout.fragment_post_item

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = getRecyclerView(view)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // TODO: create the view holder for deleted post
    sealed class PostViewHolder(mView: View, itemTouchHelper: ItemTouchHelper) : RecyclerView.ViewHolder(mView) {
        val avatarView: CircleImageView = itemView.avatarImageView.also {
            it.setOnTouchListener { _, motionEvent ->
                if (motionEvent.actionMasked == MotionEvent.ACTION_MOVE) {
                    itemTouchHelper.startSwipe(this)
                }
                false
            }
        }
        val screenNameTextView: TextView = itemView.screenNameTextView
        val bodyTextView: TextView = itemView.bodyTextView
        val dateTextView: TextView = itemView.relativeTimeTextView
        val handleNameTextView: TextView = itemView.handleNameTextView
        val repostedByTextView: TextView = itemView.repostedByTextView
        val starStateView: View = itemView.starStateView
        val repostStateView: View = itemView.repostStateView
        val thumbnailViewPager: ViewPager2 = itemView.thumbnailViewPager
        val thumbnailViewPagerFrameLayout: FrameLayout = itemView.thumbnailViewPagerFrameLayout
        val thumbnailTabLayout: TabLayout = itemView.thumbnailTabLayout

        class Exist(itemView: View, itemTouchHelper: ItemTouchHelper) : PostViewHolder(itemView, itemTouchHelper) {
            val replyTextView: TextView = itemView.replyTextView
            val starTextView: TextView = itemView.starTextView
            val repostTextView: TextView = itemView.repostTextView
            val moreImageView: ImageView = itemView.moreImageView
        }

        class Deleted(itemView: View, itemTouchHelper: ItemTouchHelper) : PostViewHolder(itemView, itemTouchHelper)
    }

    class PostItemViewModel(private val streamType: StreamType, private val getPostUseCase: GetPostUseCase) :
        BaseListFragment.BaseListViewModel<Post>() {
        override suspend fun getItems(params: PaginationParam): PnutResponse<List<Post>> {
            val getPostParam = GetPostsParam().also { it.add(params) }
            return getPostUseCase.run(GetPostInputData(streamType, getPostParam)).res
        }


        class Factory(private val streamType: StreamType, private val getPostUseCase: GetPostUseCase) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PostItemViewModel(streamType, getPostUseCase) as T
            }
        }
    }

//    override fun onRefresh() {
//        viewModel.loadNewPosts()
//    }

    class HomeStream : PostItemFragment() {
        override val streamType = StreamType.Home
    }

    class MentionsStream : PostItemFragment() {
        override val streamType = StreamType.Mentions
    }


    companion object {
        fun getHomeStreamInstance() = HomeStream()
        fun getMentionStreamInstance() = MentionsStream()
        fun getConversationInstance() = ExploreFragment.ConversationsFragment.newInstance()
        fun getMissedConversationInstance() = ExploreFragment.MissedConversationsFragment.newInstance()
        fun getNewcomersInstance() = ExploreFragment.NewcomersFragment.newInstance()
        fun getPhotoInstance() = ExploreFragment.PhotosFragment.newInstance()
        fun getTrendingInstance() = ExploreFragment.TrendingFragment.newInstance()
        fun getGlobalInstance() = ExploreFragment.GlobalFragment.newInstance()
        fun getTaggedStreamInstance(tag: String) = TagStreamFragment.newInstance(tag)

        fun getUserPostInstance(userId: String) = SpecificUserPostFragment.UserPostFragment.newInstance(userId)
        fun getStarInstance(userId: String = "me") = SpecificUserPostFragment.StarsPostFragment.newInstance(userId)

        fun getThreadInstance(post: Post) = ThreadFragment.newInstance(post)
    }
}