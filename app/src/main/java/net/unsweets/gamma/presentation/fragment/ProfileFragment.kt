package net.unsweets.gamma.presentation.fragment


import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
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
import com.google.android.material.appbar.AppBarLayout
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentProfileBinding
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.usecases.GetProfileUseCase
import net.unsweets.gamma.presentation.adapter.ProfilePagerAdapter
import net.unsweets.gamma.presentation.util.ComputedLiveData
import net.unsweets.gamma.presentation.util.EntityOnTouchListener
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.util.SingleLiveEvent
import java.util.*
import javax.inject.Inject
import kotlin.collections.set

class ProfileFragment : BaseFragment() {
    private enum class BundleKey {
        ID, IconUrl, User, IconTransitionName
    }

    @Inject
    lateinit var getProfileUseCase: GetProfileUseCase
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this, ProfileViewModel.Factory(activity!!.application, getProfileUseCase))
            .get(ProfileViewModel::class.java)
    }

    private val userId: String by lazy {
        arguments?.getString(BundleKey.ID.name, "") ?: ""
    }

    private lateinit var binding: FragmentProfileBinding

    private val entityOnTouchListener: View.OnTouchListener = EntityOnTouchListener()

    private val eventObserver = Observer<Event> { eventHandling(it) }
    private val userObserver = Observer<User> {
        if (it == null || it.content.coverImage.isDefault) return@Observer
        viewModel.iconUrl.postValue(it.content.avatarImage.link)
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event.observe(this, eventObserver)
        viewModel.user.observe(this, userObserver)
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
            toolbar.inflateMenu(R.menu.profile)
            toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            //            viewModel.getUserData(userId)
        }
        binding.profileDescriptionTextView.setOnTouchListener(entityOnTouchListener)
        val pagerAdapter = ProfilePagerAdapter(context!!, childFragmentManager, userId)
        binding.profileViewPager.adapter = pagerAdapter
        binding.profileViewPagerTab.setupWithViewPager(binding.profileViewPager)

        toolbarSetup(binding.appBar, binding.swipeRefreshLayout)
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                sharedElements[getString(R.string.icon_transition)] = binding.circleImageView
            }
        })

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuMessage -> openMessageFragment()
            R.id.menuBlock -> toggleBlock()
            R.id.menuMute -> toggleMute()
            R.id.menuShare -> share()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun share() {
    }

    private fun toggleMute() {
    }

    private fun toggleBlock() {
    }

    private fun openMessageFragment() {
    }

    private fun fixTransition(iconUrl: String) {
        GlideApp.with(context!!)
            .load(iconUrl)
//            .addListener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    startPostponedEnterTransition()
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    startPostponedEnterTransition()
//                    return false
//                }
//            })
            .onlyRetrieveFromCache(true)
            .into(binding.circleImageView)
        viewModel.iconUrl.value = iconUrl

    }

    private fun toolbarSetup(appBarLayout: AppBarLayout, swipeRefreshLayout: SwipeRefreshLayout) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar: AppBarLayout, offset: Int ->
            val per = Math.abs(offset).toFloat() / appBar.totalScrollRange.toFloat() * 255
            val textColor = viewModel.toolbarTextColor.value ?: return@OnOffsetChangedListener
            val bgColor = viewModel.toolbarBgColor.value ?: return@OnOffsetChangedListener
            swipeRefreshLayout.isEnabled = per == 0f
            viewModel.toolbarTextColor.postValue(ColorUtils.setAlphaComponent(textColor, per.toInt()))
            viewModel.toolbarBgColor.postValue(ColorUtils.setAlphaComponent(bgColor, per.toInt()))
        })
        viewModel.toolbarBgColor.value = ContextCompat.getColor(context!!, R.color.colorStatusBar)

    }

    private fun openFollowerList(user: User) {
        val fragment = UserListFragment.FollowerListFragment.newInstance(user)
        addFragment(fragment, "follower")
    }

    private fun openFollowingList(user: User) {
        val fragment = UserListFragment.FollowingListFragment.newInstance(user)
        addFragment(fragment, "following")
    }

    private fun eventHandling(it: Event?) {
        when (it) {
            is Event.FollowingList -> openFollowingList(it.user)
            is Event.FollowerList -> openFollowerList(it.user)
        }
    }

    class ProfileViewModel(val app: Application, val getProfileUseCase: GetProfileUseCase) : AndroidViewModel(app) {
        val event = SingleLiveEvent<Event>()
        val user = MutableLiveData<User>()
        val iconUrl = MutableLiveData<String>().apply { value = "" }
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
        private val me = Transformations.map(user) {
            it.followsYou && it.youFollow && !it.youCanFollow
        }
        val mainActionButtonText: LiveData<String> = ComputedLiveData.of(me, user) { me, user ->
            when {
                me == true -> app.getString(R.string.edit_profile)
                user?.followsYou == true -> app.getString(R.string.unfollow)
                user?.youBlocked == true -> app.getString(R.string.unblock)
                else -> app.getString(R.string.follow)
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

        class Factory(private val application: Application, private val getProfileUseCase: GetProfileUseCase) :
            ViewModelProvider.AndroidViewModelFactory(application) {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(application, getProfileUseCase) as T
            }
        }
    }

    sealed class Event {
        data class FollowerList(val user: User) : Event()
        data class FollowingList(val user: User) : Event()
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

