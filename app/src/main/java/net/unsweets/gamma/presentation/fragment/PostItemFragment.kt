package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
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
import java.util.*
import javax.inject.Inject


abstract class PostItemFragment : BaseListFragment<Post, PostItemFragment.PostViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<Post, PostItemFragment.PostViewHolder>,
    ThumbnailViewPagerAdapter.Listener, DeletePostDialogFragment.Callback {
    enum class BundleKey { MainPostId }

    private val mainPostId by lazy {
        arguments?.getString(BundleKey.MainPostId.name, "") ?: ""
    }

    override fun overrideOptions(options: BaseListRecyclerViewAdapter.BaseListRecyclerViewAdapterOptions<Post, PostViewHolder>): BaseListRecyclerViewAdapter.BaseListRecyclerViewAdapterOptions<Post, PostViewHolder> {
        val defaultOptions = super.overrideOptions(options)
        return defaultOptions.copy(mainItemId = mainPostId)
    }

    override fun ok(position: Int, post: Post) {
        PostService.newDeletePostIntent(context, post.id)
        adapter.removeItem(post)
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

    private val moveTransition: Transition by lazy {
        val transition =
            TransitionInflater.from(context)
                .inflateTransition(R.transition.image_shared_element_transition)
        val duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        transition.duration = duration
        transition
    }

    override lateinit var viewModel: PostItemViewModel
    private val slideToLeftIn by lazy {
        TransitionInflater.from(context).inflateTransition(R.transition.slide_to_left_in)
    }
    private val slideToLeftOut by lazy {
        TransitionInflater.from(context).inflateTransition(R.transition.slide_to_left_out)
    }
    private val postsObserver = Observer<List<Post>> {

    }

    @Inject
    lateinit var getPostUseCase: GetPostUseCase
    abstract val streamType: StreamType
    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, PostItemViewModel.Factory(streamType, getPostUseCase))
            .get(PostItemViewModel::class.java)
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null)
            currentPosition = savedInstanceState.getInt(StateKey.CurrentPosition.name, -1)

    }

    override val baseListListener by lazy { this }

    override fun createViewHolder(mView: View, viewType: Int): PostViewHolder =
        PostViewHolder(mView, itemTouchHelper)

    override fun onClickItemListener(item: Post) {
        val fragment = getThreadInstance(item, item.id)
        addFragment(fragment, item.id)
    }

    private enum class DialogKey { Compose, DeletePost }

    override fun onBindViewHolder(item: Post, viewHolder: PostViewHolder, position: Int, isMainItem: Boolean) {
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
            viewHolder.avatarView.isEnabled = false
        } else {
            viewHolder.itemView.alpha = 1f
            viewHolder.avatarView.isEnabled = true
            GlideApp.with(this).load(url).into(viewHolder.avatarView)
            val iconTransition = getString(R.string.icon_transition)
            val iconTransitionName =
                "$iconTransition + ${viewHolder.adapterPosition} ${streamType::class.java.simpleName}"
            viewHolder.avatarView.transitionName = iconTransitionName
            viewHolder.avatarView.setOnClickListener {
                currentPosition = viewHolder.adapterPosition
                val transitionMap = HashMap(
                    hashMapOf<View, String>(
                        Pair(it, it.transitionName)
                    )
                )
                val id = item.mainPost.user?.id ?: return@setOnClickListener
                val fragment = ProfileFragment.newInstance(id, url, item.mainPost.user, it.transitionName)
                sharedElementReturnTransition = moveTransition
                fragment.sharedElementEnterTransition = moveTransition
                (fragment.exitTransition as? TransitionSet)?.excludeTarget(it.transitionName, true)
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
                            val dialog = DeletePostDialogFragment.newInstance(viewHolder.adapterPosition, item.mainPost)
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
            val hasConversation = item.mainPost.replyTo != null || (item.mainPost.counts?.replies ?: 0) > 0
            viewHolder.chatIconImageView.visibility =
                if (hasConversation) View.VISIBLE else View.GONE
            if (hasConversation) {
                val res =
                    if (item.mainPost.replyTo != null) R.drawable.ic_chat_bubble_black_24dp else R.drawable.ic_chat_bubble_outline_black_24dp
                viewHolder.chatIconImageView.setImageResource(res)

            }

        }
        viewHolder.starStateView.visibility = if (item.mainPost.youBookmarked == true) View.VISIBLE else View.GONE
        viewHolder.repostStateView.visibility = if (item.mainPost.youReposted == true) View.VISIBLE else View.GONE

        viewHolder.dateTextView.text = getShortDateStr(viewHolder.itemView.context, item.mainPost.createdAt)

        viewHolder.contentsWrapperLayout.visibility = getVisibility(item.mainPost.showContents)

        val isNsfw = item.mainPost.nsfwMask
        viewHolder.nsfwMaskLayout.visibility = getVisibility(isNsfw)
        viewHolder.showNsfwButton.setOnClickListener {
            item.mainPost.nsfwMask = false
            adapter.notifyItemChanged(viewHolder.adapterPosition)
        }

        val isSpoiler = item.mainPost.spoilerMask
        viewHolder.spoilerMaskLayout.visibility = getVisibility(isSpoiler)
        val spoilerTopic = item.mainPost.spoiler?.value?.topic ?: ""
        viewHolder.showSpoilerButton.text = context.getString(R.string.show_spoiler, spoilerTopic)
        viewHolder.showSpoilerButton.setOnClickListener {
            item.mainPost.spoilerMask = false
            adapter.notifyItemChanged(viewHolder.adapterPosition)
        }

        val raw = item.mainPost.raw
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
        viewHolder.detailInfoLayout.visibility = getVisibility(isMainItem)
        val replyCount = item.mainPost.counts?.replies ?: 0
        val repostCount = item.mainPost.counts?.reposts ?: 0
        val starCount = item.mainPost.counts?.bookmarks ?: 0
        val replyText = resources.getQuantityString(R.plurals.reply_count_template, replyCount, replyCount)
        val repostText = resources.getQuantityString(R.plurals.repost_count_template, repostCount, repostCount)
        val starText = resources.getQuantityString(R.plurals.star_count_template, starCount, starCount)
        viewHolder.replyCountTextView.text = replyText
        viewHolder.repostCountTextView.text = repostText
        viewHolder.starCountTextView.text = starText

        viewHolder.itemView.let {
            it.isClickable = !isMainItem
            it.isFocusable = !isMainItem
        }
    }

    private fun getVisibility(b: Boolean): Int = if (b) View.VISIBLE else View.GONE

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

    private var currentPosition = -1

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(StateKey.CurrentPosition.name, currentPosition)
    }

    private enum class StateKey { CurrentPosition }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = getRecyclerView(view)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(currentPosition) ?: return
                if (names == null || sharedElements == null) return
                sharedElements[names[0]] = viewHolder.itemView.findViewById(R.id.avatarImageView)
            }
        })

    }

    // TODO: create the view holder for deleted post
    class PostViewHolder(mView: View, itemTouchHelper: ItemTouchHelper) : RecyclerView.ViewHolder(mView) {
        val avatarView: CircleImageView = itemView.avatarImageView.also {
            it.setOnTouchListener { view, motionEvent ->
                if (view.isEnabled && motionEvent.actionMasked == MotionEvent.ACTION_MOVE) {
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
        val replyTextView: TextView = itemView.replyTextView
        val starTextView: TextView = itemView.starTextView
        val repostTextView: TextView = itemView.repostTextView
        val moreImageView: ImageView = itemView.moreImageView
        val thumbnailViewPager: ViewPager2 = itemView.thumbnailViewPager
        val thumbnailViewPagerFrameLayout: FrameLayout = itemView.thumbnailViewPagerFrameLayout
        val thumbnailTabLayout: TabLayout = itemView.thumbnailTabLayout
        val chatIconImageView: ImageView = itemView.chatIconImageView
        val nsfwMaskLayout: FrameLayout = itemView.nsfwMaskLayout
        val showNsfwButton: MaterialButton = itemView.showNsfwButton
        val spoilerMaskLayout: FrameLayout = itemView.spoilerMaskLayout
        val showSpoilerButton: MaterialButton = itemView.showSpoilerButton
        val contentsWrapperLayout: LinearLayout = itemView.contentsWrapperLayout
        val detailInfoLayout: LinearLayout = itemView.detailInfoLayout
        val replyCountTextView: TextView = itemView.replyCountTextView
        val repostCountTextView: TextView = itemView.repostCountTextView
        val starCountTextView: TextView = itemView.starCountTextView
        val postItemForegroundView: ConstraintLayout = itemView.postItemForegroundView
    }

    class PostItemViewModel(private val streamType: StreamType, private val getPostUseCase: GetPostUseCase) :
        BaseListFragment.BaseListViewModel<Post>() {
        override suspend fun getItems(params: PaginationParam): PnutResponse<List<Post>> {
            val getPostParam = GetPostsParam(params.toMap()).also { it.add(params) }
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

    class SearchPostsFragment : PostItemFragment() {
        private val keyword by lazy {
            arguments?.getString(BundleKey.Keyword.name, "").orEmpty()
        }

        private enum class BundleKey { Keyword }

        override val streamType by lazy {
            StreamType.Search(keyword)
        }

        companion object {
            fun newInstance(keyword: String) = SearchPostsFragment().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.Keyword.name, keyword)
                }
            }
        }
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

        fun getThreadInstance(post: Post, mainPostId: String = "") = ThreadFragment.newInstance(post, mainPostId)
    }
}