package net.unsweets.gamma.presentation.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.format.DateFormat
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.ChangeBounds
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentProfileBinding
import net.unsweets.gamma.domain.Relationship
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.GetProfileInputData
import net.unsweets.gamma.domain.model.io.UpdateRelationshipInputData
import net.unsweets.gamma.domain.usecases.GetProfileUseCase
import net.unsweets.gamma.domain.usecases.UpdateRelationshipUseCase
import net.unsweets.gamma.presentation.activity.PhotoViewActivity
import net.unsweets.gamma.presentation.adapter.ProfilePagerAdapter
import net.unsweets.gamma.presentation.util.EntityOnTouchListener
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.presentation.util.ShareUtil
import net.unsweets.gamma.presentation.util.Util
import net.unsweets.gamma.util.SingleLiveEvent
import java.util.*
import javax.inject.Inject
import kotlin.collections.set
import kotlin.math.abs

class ProfileFragment : BaseFragment() {
    private enum class BundleKey {
        ID, IconUrl, User, IconTransitionName
    }

    private val userPostsRssUrl: String by lazy {
        "https://api.pnut.io/v0/feed/rss/users/$userId/posts"
    }
    private val fetchingUserObserve = Observer<Boolean> {
        binding.swipeRefreshLayout.isRefreshing = it
    }
    @Inject
    lateinit var getProfileUseCase: GetProfileUseCase
    @Inject
    lateinit var updateRelationshipUseCase: UpdateRelationshipUseCase

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(
            this,
            ProfileViewModel.Factory(
                requireActivity().application,
                getProfileUseCase,
                updateRelationshipUseCase,
                userId
            )
        )[ProfileViewModel::class.java]
    }

    private val userId: String by lazy {
        arguments?.getString(BundleKey.ID.name, "") ?: ""
    }

    private lateinit var binding: FragmentProfileBinding

    private val entityOnTouchListener: View.OnTouchListener = EntityOnTouchListener()

    private val eventObserver = Observer<Event>(::eventHandling)
    private val userObserver = Observer<User> {
        if (it == null || it.content.coverImage.isDefault) return@Observer
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
        viewModel.user.observe(this, userObserver)
        viewModel.fetchingUser.observe(this, fetchingUserObserve)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this
        arguments?.let { bundle ->
            val iconUrl = bundle.getString(BundleKey.IconUrl.name, "")
            if (iconUrl != null && iconUrl.isNotBlank()) {
                fixTransition(iconUrl)
            }
            val user = bundle.getParcelable<User?>(BundleKey.User.name)
            val iconTransitionName = bundle.getString(BundleKey.IconTransitionName.name)
            binding.circleImageView.transitionName = iconTransitionName
            viewModel.user.value = user
        }
        binding.viewModel = viewModel

        binding.toolbar.let { toolbar ->
            toolbar.setNavigationOnClickListener { backToPrevFragment() }
            toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getUser()
        }
        binding.profileDescriptionTextView.setOnTouchListener(entityOnTouchListener)
        val pagerAdapter = ProfilePagerAdapter(requireContext(), childFragmentManager, userId)
        binding.profileViewPager.adapter = pagerAdapter
        binding.profileViewPagerTab.setupWithViewPager(binding.profileViewPager)

        toolbarSetup(binding.appBar, binding.swipeRefreshLayout)
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: List<String>,
                sharedElements: MutableMap<String, View>
            ) {
                binding.circleImageView.clipToOutline = true
                sharedElements[names[0]] = binding.circleImageView
            }
        })

        val coverUrl = User.getCoverUrl(userId)
        GlideApp.with(this).load(coverUrl)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(20)))
            .into(binding.coverImageView)

//        binding.circleImageView.setShape(preferenceRepository.shapeOfAvatar)
//        binding.circleImageView.setBackgroundResource(preferenceRepository.shapeOfAvatar.drawableRes)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuMessage -> openMessageFragment()
            R.id.menuBlock -> toggleBlock()
            R.id.menuMute -> toggleMute()
            R.id.menuShare -> share()
            R.id.menuShareUserPostsRss -> shareUserPostsRss()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun shareUserPostsRss() {
        activity?.let { ShareUtil.launchShareUrlIntent(it, userPostsRssUrl) }
    }

    private fun share() {
        val username = viewModel.user.value?.username ?: return
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, User.getCanonicalUrl(username))
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.share)))
    }

    private fun toggleMute() {
    }

    private fun toggleBlock() {
    }

    private fun openMessageFragment() {
    }

    private fun fixTransition(iconUrl: String) {
        GlideApp.with(requireContext())
            .load(iconUrl)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .onlyRetrieveFromCache(true)
            .into(binding.circleImageView)

    }

    private fun toolbarSetup(appBarLayout: AppBarLayout, swipeRefreshLayout: SwipeRefreshLayout) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar: AppBarLayout, offset: Int ->
            val per = abs(offset).toFloat() / appBar.totalScrollRange.toFloat() * 255
            val textColor = viewModel.toolbarTextColor.value ?: return@OnOffsetChangedListener
            val bgColor = viewModel.toolbarBgColor.value ?: return@OnOffsetChangedListener
            swipeRefreshLayout.isEnabled = per == 0f
            viewModel.toolbarTextColor.postValue(
                ColorUtils.setAlphaComponent(
                    textColor,
                    per.toInt()
                )
            )
            viewModel.toolbarBgColor.postValue(ColorUtils.setAlphaComponent(bgColor, per.toInt()))
        })
        viewModel.toolbarBgColor.value = ContextCompat.getColor(requireContext(), R.color.colorStatusBar)

    }

    private fun openFollowerList(user: User) {
        val fragment = FollowingFollowerListFragment.FollowerListFragment.newInstance(user)
        addFragment(fragment, "follower")
    }

    private fun openFollowingList(user: User) {
        val fragment = FollowingFollowerListFragment.FollowingListFragment.newInstance(user)
        addFragment(fragment, "following")
    }

    private fun eventHandling(it: Event?) {
        when (it) {
            is Event.FollowingList -> openFollowingList(it.user)
            is Event.FollowerList -> openFollowerList(it.user)
            is Event.EditProfile -> showEditProfileDialog()
            is Event.ShowAvatar -> it.url?.let { url -> showAvatar(url) }
            is Event.ShowCover -> it.url?.let { url -> showCover(url) }
            is Event.OpenVerifiedDomain -> openVerifiedDomain(it.url)
        }
    }

    private fun openVerifiedDomain(url: String) {
        context?.let { Util.openCustomTabUrl(it, url) }
    }

    private enum class TransitionName { Avatar, Cover }

    private fun showCover(url: String) {
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)
        PhotoViewActivity.startActivity(
            activity,
            url,
            binding.coverImageView,
            TransitionName.Cover.name
        )
    }

    private fun showAvatar(url: String) {
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)
        PhotoViewActivity.startActivity(
            activity,
            url,
            binding.circleImageView,
            TransitionName.Avatar.name
        )
    }

    private enum class DialogKey { EditProfile }
    private enum class RequestCode { UpdateProfile }

    private fun showEditProfileDialog() {
        val fm = parentFragmentManager
        val fragment = EditProfileFragment.newInstance(userId).also {
            //            val transition =
//                TransitionInflater.from(context).inflateTransition(R.transition.edit_profile)
            sharedElementEnterTransition = ChangeBounds()
        }
        fragment.setTargetFragment(this, RequestCode.UpdateProfile.ordinal)
        // TODO: fix fragment transition
        val ft = fm.beginTransaction()
            .addSharedElement(
                binding.userMainActionButton,
                binding.userMainActionButton.transitionName
            )
        fragment.show(ft, DialogKey.EditProfile.name)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.UpdateProfile.ordinal -> {
                if (resultCode != Activity.RESULT_OK || data == null) return
                viewModel.user.value = EditProfileFragment.parseResultIntent(data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    class ProfileViewModel(
        private val app: Application,
        private val getProfileUseCase: GetProfileUseCase,
        private val updateRelationshipUseCase: UpdateRelationshipUseCase,
        private val userId: String?
    ) : AndroidViewModel(app) {
        val event = SingleLiveEvent<Event>()
        val user = MutableLiveData<User>()
        val iconUrl = Transformations.map(user) {
            when {
                it != null -> it.getAvatarUrl(null)
                userId != null -> User.getAvatarUrl(userId, null)
                else -> ""
            }
        }
        val usernameWithAt: LiveData<String> = Transformations.map(user) { "@${it?.username}" }
        val since: LiveData<CharSequence?> = Transformations.map(user) {
            val calendar = Calendar.getInstance()
            if (it != null) calendar.time = it.createdAt
            DateFormat.format("yyyy/MM/dd", calendar)
        }
        val relation: LiveData<Int> = Transformations.map(user) {
            if (it == null) return@map 0
            if (it.youFollow && it.followsYou && !it.youCanFollow) {
                // it's me!
                R.string.its_me
            } else if (it.followsYou) {
                R.string.follows_you
            } else {
                0
            }
        }

        val toolbarTextColor = MutableLiveData<Int>().apply { value = Color.WHITE }
        val toolbarBgColor = MutableLiveData<Int>().apply { value = Color.TRANSPARENT }
        val loading = MutableLiveData<Boolean>().apply { value = false }
        val mainActionButtonText: LiveData<String> = Transformations.map(user) {
            when {
                it == null -> ""
                it.me -> app.getString(R.string.edit_profile)
                it.youFollow -> app.getString(R.string.unfollow)
                it.youBlocked -> app.getString(R.string.unblock)
                else -> app.getString(R.string.follow)
            }
        }
        val fetchingUser = MutableLiveData<Boolean>().apply { value = false }
        val actionButtonTextColor = Transformations.map(user) {
            when (it?.youFollow) {
                false -> Util.getAccentColor(app)
                else -> Util.getWindowBackgroundColor(app)
            }
        }
        val actionButtonTintColor = Transformations.map(user) {
            when (it?.youFollow) {
                true -> Util.getAccentColor(app)
                else -> Util.getWindowBackgroundColor(app)
            }
        }
        val actionButtonRippleColor = Util.getPrimaryColorDark(app)
        val verifiedDomainVisibility = Transformations.map(user) {
            if (it?.verified != null) View.VISIBLE else View.GONE
        }

        init {
            if (user.value == null) getUser()
        }

        fun openVerifiedDomain() =
            user.value?.verified?.let { event.emit(Event.OpenVerifiedDomain(it.link)) }

        fun getUser() {
            val id = userId ?: user.value?.id ?: return
            fetchingUser.value = true
            viewModelScope.launch {
                runCatching {
                    getProfileUseCase.run(GetProfileInputData(id))
                }.onSuccess {
                    user.postValue(it.res.data)
                }
                fetchingUser.postValue(false)
            }
        }

        fun openFollowerList() {
            val user = user.value ?: return
            event.value = Event.FollowerList(user)
        }

        fun openFollowingList() {
            val user = user.value ?: return
            event.value = Event.FollowingList(user)
        }

        fun mainAction() {
            when {
                user.value?.me == true -> {
                    event.value = Event.EditProfile
                }
                user.value?.youFollow == false -> follow()
                else -> unfollow()
            }
        }

        fun showAvatar() = event.emit(Event.ShowAvatar(user.value?.getAvatarUrl(null)))
        fun showCover() = event.emit(Event.ShowCover(user.value?.content?.coverImage?.link))
        private fun follow() = updateRelationship(true)
        private fun unfollow() = updateRelationship(false)
        private fun updateRelationship(follow: Boolean) {
            viewModelScope.launch {
                runCatching {
                    loading.postValue(true)
                    val user = user.value ?: return@launch
                    val relationship = if (follow) Relationship.Follow else Relationship.UnFollow
                    updateRelationshipUseCase.run(
                        UpdateRelationshipInputData(
                            user.id,
                            relationship
                        )
                    )
                }.onSuccess {
                    user.postValue(it.res.data)
                }
                loading.postValue(false)
            }
        }

        class Factory(
            private val application: Application,
            private val getProfileUseCase: GetProfileUseCase,
            private val updateRelationshipUseCase: UpdateRelationshipUseCase,
            private val userId: String?
        ) :
            ViewModelProvider.AndroidViewModelFactory(application) {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(
                    application,
                    getProfileUseCase,
                    updateRelationshipUseCase,
                    userId
                ) as T
            }
        }
    }

    sealed class Event {
        object EditProfile : ProfileFragment.Event()
        data class FollowerList(val user: User) : Event()
        data class FollowingList(val user: User) : Event()
        data class ShowAvatar(val url: String?) : Event()
        data class ShowCover(val url: String?) : Event()
        data class OpenVerifiedDomain(val url: String) : Event()
    }

    companion object {
        fun newInstance(
            id: String,
            iconUrl: String? = null,
            user: User? = null,
            iconTransitionName: String? = null
        ): Fragment = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(BundleKey.ID.name, id)
                putString(BundleKey.IconUrl.name, iconUrl)
                putParcelable(BundleKey.User.name, user)
                putString(BundleKey.IconTransitionName.name, iconTransitionName)
            }
        }
    }
}

