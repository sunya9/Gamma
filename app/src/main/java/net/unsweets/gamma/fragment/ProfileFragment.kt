package net.unsweets.gamma.fragment


import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import net.unsweets.gamma.model.User
import net.unsweets.gamma.util.Store
import net.unsweets.gamma.util.then
import java.util.*

class ProfileFragment : BaseFragment(), BaseListFragment.OnBaseListListener {
    private enum class ProfileArgKey {
        ID
    }

    private lateinit var viewModel: ProfileViewModel

    private lateinit var mId: String

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentProfileBinding>(inflater, R.layout.fragment_profile, container, false)
        binding.setLifecycleOwner(this)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.event.observe(this, Observer { eventHandling(it) })
        binding.viewModel = viewModel

        binding.toolbar.setNavigationOnClickListener { backToPrevFragment() }

        arguments?.let { bundle ->
            val id = bundle.getString(ProfileArgKey.ID.name) ?: return@let
            mId = id
            getUserData(id)
        } ?: backToPrevFragment()
        binding.swipeRefreshLayout.setOnRefreshListener {
            getUserData(mId)
        }
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.list, PostItemFragment.getUserPostInstance(mId))
            ?.commit()
        binding.list

        toolbarSetup(binding.appBar, binding.swipeRefreshLayout)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<View>(android.R.id.content)
            ?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        activity?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

    }

    override fun onRefreshed() {
        binding.swipeRefreshLayout.isRefreshing = false

    }

    private fun getUserData(id: String) {
        pnut.getUser(id).then { viewModel.user.postValue(it.data) }
        viewModel.me.value = prefManager.getDefaultAccountID() == id
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
        viewModel.toolbarBgColor.value = ContextCompat.getColor(context!!, R.color.colorAccent)

    }

    private fun openFollowerList() {
        val user = viewModel.user.value ?: return
        val fragment = UserListFragment.newInstance(UserListFragment.UserListMode.FOLLOWERS, user)
        addFragment(fragment, null)
    }

    private fun openFollowingList() {
        val user = viewModel.user.value ?: return
        val fragment = UserListFragment.newInstance(UserListFragment.UserListMode.FOLLOWING, user)
        addFragment(fragment, null)
    }

    private fun eventHandling(it: Event?) {
        when (it) {
            Event.FOLLOWING_LIST -> openFollowingList()
            Event.FOLLOWER_LIST -> openFollowerList()
        }
    }

    class ProfileViewModel : Store<Event>() {
        val user = MutableLiveData<User>()
        val usernameWithAt: LiveData<String> = Transformations.map(user) { "@${it.username}" }
        val since: LiveData<CharSequence> = Transformations.map(user) {
            val calendar = Calendar.getInstance()
            calendar.time = it.createdAt
            DateFormat.format("yyyy/MM/dd", calendar)
        }
        val relation: LiveData<Int> = Transformations.map(user) {
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
        fun openFollowerList() = sendEvent(Event.FOLLOWER_LIST)
        fun openFollowingList() = sendEvent(Event.FOLLOWING_LIST)
    }

    enum class Event {
        FOLLOWER_LIST, FOLLOWING_LIST
    }

    companion object {
        fun newInstance(id: String): Fragment = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ProfileArgKey.ID.name, id)
            }
        }
    }
}

