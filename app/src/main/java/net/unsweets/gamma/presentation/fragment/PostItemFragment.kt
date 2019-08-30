package net.unsweets.gamma.presentation.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_post_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.broadcast.PostReceiver
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.entity.raw.LongPost
import net.unsweets.gamma.domain.entity.raw.OEmbed
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.GetPostInputData
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.usecases.GetPostUseCase
import net.unsweets.gamma.presentation.activity.PhotoViewActivity
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.adapter.ReactionUsersAdapter
import net.unsweets.gamma.presentation.adapter.ThumbnailViewPagerAdapter
import net.unsweets.gamma.presentation.util.*
import net.unsweets.gamma.service.PostService
import java.util.*
import javax.inject.Inject


abstract class PostItemFragment : BaseListFragment<Post, PostItemFragment.PostViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<Post, PostItemFragment.PostViewHolder>,
    ThumbnailViewPagerAdapter.Listener, DeletePostDialogFragment.Callback,
    SimpleBottomSheetMenuFragment.Callback, PostReceiver.Callback {
    override fun onPostReceive(post: Post) {

    }

    override fun onStarReceive(post: Post) {
        val viewHolder = getRecyclerView(
            view ?: return
        ).findViewHolderForItemId(post.id.toLong()) as? PostViewHolder ?: return
        updatePost(post)
        updateStarView(viewHolder, post)
    }

    private fun updatePost(post: Post) {
        adapter.updateItem(post)
    }

    override fun onRepostReceive(post: Post) {
        val viewHolder = getRecyclerView(
            view ?: return
        ).findViewHolderForItemId(post.id.toLong()) as? PostViewHolder ?: return
        updatePost(post)
        updateRepostView(viewHolder, post)
    }

    override fun onDeletePostReceive(post: Post) {
    }

    private val receiverManager by lazy {
        activity?.let { LocalBroadcastManager.getInstance(it.applicationContext) }
    }

    private val postReceiver by lazy {
        PostReceiver(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        receiverManager?.registerReceiver(postReceiver, PostService.getIntentFilter())
    }

    override fun onDetach() {
        super.onDetach()
        receiverManager?.unregisterReceiver(postReceiver)
    }

    enum class BundleKey { MainPostId }

    override fun onMenuShow(menu: Menu, tag: String?) {
    }

    override fun onMenuItemSelected(menuItem: MenuItem, tag: String?) {
        when (tag) {
            DialogKey.More.name -> handlePostMoreMenu(menuItem)
        }
    }

    private fun handlePostMoreMenu(menuItem: MenuItem) {
        val post = selectedPost ?: return
        when (menuItem.itemId) {
            R.id.menuShare -> showShareMenu(post)
        }
    }

    private fun showShareMenu(post: Post) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.canonicalUrl)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.share)))
    }

    private var expandedPostItemId = ""

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
        viewModel = ViewModelProvider(
            this,
            PostItemViewModel.Factory(streamType, getPostUseCase)
        )[PostItemViewModel::class.java]
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(StateKey.CurrentPosition.name, -1)
            selectedPost = savedInstanceState.getParcelable<Post>(StateKey.SelectedPost.name)
        }

    }

    override val baseListListener by lazy { this }

    override fun createViewHolder(mView: View, viewType: Int): PostViewHolder =
        PostViewHolder(mView, itemTouchHelper)

    override fun onClickItemListener(item: Post) {
        val recyclerView = getRecyclerView(view ?: return)
        val clickedItemPosition =
            calcPosition(recyclerView.findViewHolderForItemId(item.id.toLong())?.adapterPosition ?: return)
        adapter.notifyItemChanged(clickedItemPosition)
        expandedPostItemId = when {
            expandedPostItemId.isNotEmpty() -> when {
                expandedPostItemId != item.id -> {
                    // click another item when already expanded
                    recyclerView.findViewHolderForItemId(expandedPostItemId.toLong())?.adapterPosition?.let {
                        adapter.notifyItemChanged(calcPosition(it))
                    }
                    item.id
                }
                else -> // click same item
                    ""
            }
            else -> item.id
        }
    }

    private fun calcPosition(position: Int): Int {
        return if (reverse) position - -1 else position
    }

    var selectedPost: Post? = null

    private fun showMoreMenu(post: Post) {
        selectedPost = post
        val fragment = SimpleBottomSheetMenuFragment.newInstance(R.menu.post_item_more)
        fragment.show(childFragmentManager, DialogKey.More.name)
    }

    private enum class DialogKey { Compose, DeletePost, More, LongPost }


    override fun onBindViewHolder(item: Post, viewHolder: PostViewHolder, position: Int, isMainItem: Boolean) {
        val context = viewHolder.itemView.context
        viewHolder.itemId
        val isDeleted = item.isDeleted == true
        viewHolder.postItemForegroundView.alpha = if (isDeleted) 0.5f else 1f
        val bgColor =
            if (isDeleted) context.getColor(R.color.colorWindowBackground) else Util.getPrimaryColor(
                context
            )
        viewHolder.swipeActionsLayout.setBackgroundColor(bgColor)
        viewHolder.screenNameTextView.text = item.mainPost.user?.username ?: getString(R.string.deleted_post_user_name)
        viewHolder.handleNameTextView.text = item.mainPost.user?.name.orEmpty()
        viewHolder.avatarView.isEnabled = !isDeleted

        updateStarView(viewHolder, item)
        viewHolder.actionReplyImageView.setOnClickListener {
            showReplyCompose(viewHolder.avatarView, item)
        }
        viewHolder.replyButton.isEnabled = !isDeleted
        viewHolder.replyButton.setOnClickListener {
            showReplyCompose(viewHolder.avatarView, item)
        }

        updateRepostView(viewHolder, item)

        val hasConversation = item.mainPost.replyTo != null || (item.mainPost.counts?.replies ?: 0) > 0
        viewHolder.chatIconImageView.visibility =
            if (hasConversation) View.VISIBLE else View.GONE
        if (hasConversation) {
            val res =
                if (item.mainPost.replyTo != null) R.drawable.ic_chat_bubble_black_24dp else R.drawable.ic_chat_bubble_outline_black_24dp
            viewHolder.chatIconImageView.setImageResource(res)
        }

        viewHolder.bodyTextView.text =
            item.mainPost.content?.getSpannableStringBuilder(context) ?: getString(R.string.this_post_has_deleted)

        val url = item.mainPost.user?.getAvatarUrl(User.AvatarSize.Large).orEmpty()
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

        viewHolder.dateTextView.text =
            if (!isMainItem) DateUtil.getShortDateStr(viewHolder.itemView.context, item.mainPost.createdAt) else ""
        viewHolder.absoluteDateTextView.text = DateUtil.getShortDateStrWithTime(context, item.mainPost.createdAt)
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
        val replyText = resources.getQuantityString(R.plurals.reply_count_template, replyCount, replyCount)

        viewHolder.replyCountTextView.text = replyText

        viewHolder.rootCardView.let {
            it.isClickable = !isMainItem
            it.isFocusable = !isMainItem
            it.elevation = if (isMainItem) resources.getDimension(R.dimen.elevation_main_item) else 0f
            val padding = if (isMainItem) resources.getDimensionPixelSize(R.dimen.pad_main_item) else 0
            it.setPadding(0, padding, 0, padding)

        }
        viewHolder.rootCardView.setOnLongClickListener {
            showThread(item)
        }
        viewHolder.reactionUsersRecyclerView.also {
            it.adapter = if (isMainItem) ReactionUsersAdapter(
                item.mainPost.reactionUsers,
                reactionUsersAdapterListener
            ) else null
        }
        viewHolder.clientNameTextView.text = item.mainPost.source?.name
        viewHolder.clientNameTextView.setOnClickListener {
            item.mainPost.source?.link?.let { link -> Util.openCustomTabUrl(context, link) }
        }
        viewHolder.foregroundActionsLayout.visibility =
            getVisibility(mainPostId == item.id || expandedPostItemId == item.id)
        viewHolder.threadButton.let {
            it.setOnClickListener { showThread(item) }
            it.visibility = getVisibility(item.mainPost.id != mainPostId)
        }
        viewHolder.actionThreadImageView.let {
            it.setOnClickListener { showThread(item) }
//            it.visibility = getVisibility(item.mainPost.id != mainPostId)
        }
        viewHolder.isMainItem = item.id == mainPostId
        viewHolder.moreButton.setOnClickListener { showMoreMenu(item) }
        viewHolder.actionMoreImageView.setOnClickListener { showMoreMenu(item) }

        val longPost = LongPost.findLongPost(raw)
        viewHolder.showLongPostButton.visibility = getVisibility(longPost != null)
        if (longPost != null) {
            viewHolder.showLongPostButton.setOnClickListener {
                val fragment = LongPostDialogFragment.newInstance(longPost)
                fragment.show(childFragmentManager, DialogKey.LongPost.name)
            }
        }
        val revisionType = when {
            item.mainPost.revision != null -> RevisionType.Original
            item.mainPost.isRevised == true -> RevisionType.Revised
            else -> RevisionType.None
        }
        revisionType.iconRes?.let { viewHolder.revisedIconImageView.setImageResource(it) }
        viewHolder.revisedIconImageView.visibility = getVisibility(revisionType.iconRes != null)
    }

    private fun updateRepostView(viewHolder: PostViewHolder, item: Post) {
        val repostType = when {
            item.mainPost.youReposted == true -> RepostButtonType.DeleteRepost
            item.mainPost.user?.me == true -> RepostButtonType.DeletePost
            else -> RepostButtonType.Repost
        }
        viewHolder.actionRepostImageView.let {
            //                it.setText(repostType.textRes)
            it.setImageResource(repostType.iconRes)
            it.drawable?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            it.setOnClickListener {
                toggleRepost(repostType, item.mainPost, viewHolder.adapterPosition)
            }
        }
        viewHolder.repostButton.isEnabled = !item.isDeletedNonNull
        viewHolder.repostButton.let {
            it.setImageResource(repostType.iconRes)
            it.setOnClickListener {
                toggleRepost(repostType, item.mainPost, viewHolder.adapterPosition)
            }
        }
        setupRepostView(item, viewHolder.repostedByTextView)

        viewHolder.repostStateView.visibility =
            if (item.mainPost.youReposted == true) View.VISIBLE else View.GONE
        val repostCount = item.mainPost.counts?.reposts ?: 0
        val repostText =
            resources.getQuantityString(R.plurals.repost_count_template, repostCount, repostCount)
        viewHolder.repostCountTextView.text = repostText
    }

    private fun updateStarView(viewHolder: PostViewHolder, item: Post) {
        val starDrawableRes =
            if (item.mainPost.youBookmarked == true) R.drawable.ic_star_black_24dp else R.drawable.ic_star_border_black_24dp
        viewHolder.actionStarImageView.let {
            it.setOnClickListener {
                toggleStar(item, viewHolder.adapterPosition)

            }
//                val starTextRes = if (item.mainPost.youBookmarked == true) R.string.unstar else R.string.star
//                it.text = context.getString(starTextRes)
            it.setImageResource(starDrawableRes)
            it.drawable?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        }
        viewHolder.starButton.isEnabled = !item.isDeletedNonNull
        viewHolder.starButton.let {
            it.setOnClickListener {
                toggleStar(item, viewHolder.adapterPosition)
            }
            it.setImageResource(starDrawableRes)
        }
        viewHolder.starStateView.visibility =
            if (item.mainPost.youBookmarked == true) View.VISIBLE else View.GONE
        val starCount = item.mainPost.counts?.bookmarks ?: 0
        val starText =
            resources.getQuantityString(R.plurals.star_count_template, starCount, starCount)
        viewHolder.starCountTextView.text = starText

    }

    private enum class RevisionType(val iconRes: Int?) {
        None(null), Revised(R.drawable.ic_create_black_24dp), Original(R.drawable.ic_outline_create_24dp)
    }

    private fun toggleRepost(repostType: RepostButtonType, item: Post, adapterPosition: Int) {
        when (repostType) {
            RepostButtonType.DeletePost -> {
                if (item.mainPost.user?.me == false) return
                val dialog = DeletePostDialogFragment.newInstance(adapterPosition, item.mainPost)
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
        adapter.notifyItemChanged(adapterPosition)
    }

    private fun showReplyCompose(view: View, item: Post) {
        val pos = Util.getViewPositionOnScreen(view)
        val fragment = ComposePostDialogFragment.replyInstance(pos.first, pos.second, item.mainPost)
        fragment.show(childFragmentManager, DialogKey.Compose.name)
    }

    private fun toggleStar(item: Post, adapterPosition: Int) {
        val newState = item.mainPost.youBookmarked == false
        PostService.newStarIntent(context, item.mainPost.id, newState)
        // TODO: revert state when raised error
        // star "this post"
        item.mainPost.youBookmarked = newState
        adapter.notifyItemChanged(adapterPosition)
    }

    private fun showThread(item: Post): Boolean {
        val fragment = getThreadInstance(item, item.id)
        return addFragment(fragment, item.id) == null
    }

    private val reactionUsersAdapterListener by lazy {
        object : ReactionUsersAdapter.Listener {
            override fun onUserClick(user: User) {
                val fragment = ProfileFragment.newInstance(user.id, user.getAvatarUrl(), user)
                addFragment(fragment, user.username)
            }
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
        val visibility = if (item.repostOf != null && originalUser != null) {
            repostedByTextView.setOnClickListener {
                val fragment = ProfileFragment.newInstance(originalUser.id)
                addFragment(fragment, originalUser.id)
            }
            repostedByTextView.text =
                repostedByTextView.context.getString(R.string.reposted_by_template, originalUser.username)
            true
        } else {
            false
        }
        repostedByTextView.visibility = getVisibility(visibility)
    }

    override fun getItemLayout(): Int = R.layout.fragment_post_item

    private var currentPosition = -1

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(StateKey.CurrentPosition.name, currentPosition)
        outState.putParcelable(StateKey.SelectedPost.name, selectedPost)
    }

    private enum class StateKey { CurrentPosition, SelectedPost }

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
    @SuppressLint("ClickableViewAccessibility")
    class PostViewHolder(
        mView: View,
        itemTouchHelper: ItemTouchHelper
    ) : RecyclerView.ViewHolder(mView) {
        val rootCardView: CardView = itemView.rootCardView
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
        val actionReplyImageView: ImageView = itemView.actionReplyImageView
        val actionStarImageView: ImageView = itemView.actionStarImageView
        val actionRepostImageView: ImageView = itemView.actionRepostImageView
        val actionThreadImageView: ImageView = itemView.actionThreadImageView
        val actionMoreImageView: ImageView = itemView.actionMoreImageView
        val thumbnailViewPager: ViewPager2 = itemView.thumbnailViewPager
        val thumbnailViewPagerFrameLayout: FrameLayout = itemView.thumbnailViewPagerFrameLayout
        val thumbnailTabLayout: TabLayout = itemView.thumbnailTabLayout
        val chatIconImageView: ImageView = itemView.chatIconImageView
        val nsfwMaskLayout: FrameLayout = itemView.nsfwMaskLayout
        val showNsfwButton: MaterialButton = itemView.showNsfwButton
        val spoilerMaskLayout: FrameLayout = itemView.spoilerMaskLayout
        val showSpoilerButton: MaterialButton = itemView.showSpoilerButton
        val contentsWrapperLayout: LinearLayout = itemView.contentsWrapperLayout
        val detailInfoLayout: ConstraintLayout = itemView.detailInfoLayout
        val replyCountTextView: TextView = itemView.replyCountTextView
        val repostCountTextView: TextView = itemView.repostCountTextView
        val starCountTextView: TextView = itemView.starCountTextView
        val postItemForegroundView: ConstraintLayout = itemView.postItemForegroundView
        val reactionUsersRecyclerView: RecyclerView = itemView.reactionUsersRecyclerView
        val clientNameTextView: TextView = itemView.clientNameTextView
        val foregroundActionsLayout: LinearLayout = itemView.foregroundActionsLayout
        val replyButton: ImageButton = itemView.replyButton
        val starButton: ImageButton = itemView.starButton
        val repostButton: ImageButton = itemView.repostButton
        val threadButton: ImageButton = itemView.threadButton
        val moreButton: ImageButton = itemView.moreButton
        var isMainItem: Boolean = false
        val showLongPostButton: MaterialButton = itemView.showLongPostButton
        val revisedIconImageView: ImageView = itemView.revisedIconImageView
        val swipeActionsLayout: FrameLayout = itemView.swipeActionsLayout
        val absoluteDateTextView: TextView = itemView.absoluteDateTextView

        init {
            addSpacerDecoration()
            bodyTextView.setOnTouchListener(EntityOnTouchListener())
        }

        private fun addSpacerDecoration() {
            val context = itemView.context
            val drawable = ContextCompat.getDrawable(context, R.drawable.spacer_width_half) ?: return
            val reactionSpacerDecoration = DividerItemDecoration(context, RecyclerView.HORIZONTAL).also {
                it.setDrawable(drawable)
            }
            reactionUsersRecyclerView.addItemDecoration(reactionSpacerDecoration)
        }
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