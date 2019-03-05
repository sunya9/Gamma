package net.unsweets.gamma.ui.fragment


import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentProfileBinding
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.ui.base.BaseViewModel
import net.unsweets.gamma.ui.util.EntityOnTouchListener
import net.unsweets.gamma.ui.util.GlideApp
import net.unsweets.gamma.ui.util.toLiveData
import java.util.*
import kotlin.collections.set

class ProfileFragment : BaseFragment(), BaseListFragment.OnBaseListListener {
    private enum class ProfileArgKey {
        ID, IconUrl, User, IconTransitionName
    }

    private lateinit var viewModel: ProfileViewModel

    private lateinit var mId: String

    private lateinit var binding: FragmentProfileBinding

    private val entityOnTouchListener: View.OnTouchListener = EntityOnTouchListener()

    private val eventObserver = Observer<Event> { eventHandling(it) }
    private val userObserver = Observer<User> {
        if (it == null) return@Observer
        viewModel.iconUrl.postValue(it.content.avatarImage.link)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        if (savedInstanceState == null) {
            viewModel.event.observe(this, eventObserver)
            viewModel.user.observe(this, userObserver)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this
        arguments?.let { bundle ->
            val id = bundle.getString(ProfileArgKey.ID.name) ?: return@let
            mId = id
            val iconUrl = bundle.getString(ProfileArgKey.IconUrl.name, "")
            if (iconUrl != null && iconUrl.isNotBlank()) {
                fixTransition(iconUrl)
            }
            val user = bundle.getParcelable<User?>(ProfileArgKey.User.name)
            val iconTransitionName = bundle.getString(ProfileArgKey.IconTransitionName.name)
            binding.circleImageView.transitionName = iconTransitionName
            viewModel.user.value = user
            viewModel.getUserData(id)
            viewModel.me.value = prefManager.getDefaultAccountID() == id

        }
        binding.viewModel = viewModel

        binding.toolbar.setNavigationOnClickListener { backToPrevFragment() }


        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getUserData(mId)
        }
        binding.profileDescriptionTextView.setOnTouchListener(entityOnTouchListener)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.profilePostList, PostItemFragment.getUserPostInstance(mId)).commit()
        }

        toolbarSetup(binding.appBar, binding.swipeRefreshLayout)
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: List<String>, sharedElements: MutableMap<String, View>) {
                sharedElements[getString(R.string.icon_transition)] = binding.circleImageView
            }
        })

        return binding.root
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

    override fun onRefreshed() {
        binding.swipeRefreshLayout.isRefreshing = false

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

    private fun openFollowerList() {
        val user = viewModel.user.value ?: return
        val fragment = UserListFragment.followerList(user)
        addFragment(fragment, "follower")
    }

    private fun openFollowingList() {
        val user = viewModel.user.value ?: return
        val fragment = UserListFragment.followingList(user)
        addFragment(fragment, "following")
    }

    private fun eventHandling(it: Event?) {
        when (it) {
            Event.FollowingList -> openFollowingList()
            Event.FollowerList -> openFollowerList()
        }
    }

    class ProfileViewModel(app: Application) : BaseViewModel<Event>(app) {
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
        val me = MutableLiveData<Boolean?>().apply { value = null }
        fun openFollowerList() = sendEvent(Event.FollowerList)
        fun openFollowingList() = sendEvent(Event.FollowingList)

        fun getUserData(id: String) = pnut.getUser(id).toLiveData(user)

    }

    enum class Event {
        FollowerList, FollowingList
    }

    companion object {
        fun newInstance(
            id: String,
            iconUrl: String? = null,
            user: User? = null,
            iconTransitionName: String? = null
        ): Fragment = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ProfileArgKey.ID.name, id)
                putString(ProfileArgKey.IconUrl.name, iconUrl)
                putParcelable(ProfileArgKey.User.name, user)
                putString(ProfileArgKey.IconTransitionName.name, iconTransitionName)
            }
        }
    }
}

