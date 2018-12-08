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
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_profile.*

import net.unsweets.gamma.R
import net.unsweets.gamma.adapter.PostItemRecyclerViewAdapter
import net.unsweets.gamma.databinding.FragmentProfileBinding
import net.unsweets.gamma.model.Post
import net.unsweets.gamma.model.User
import net.unsweets.gamma.util.PrefManager
import net.unsweets.gamma.util.Store
import net.unsweets.gamma.util.then
import java.util.*

class ProfileFragment : BaseFragment() {
    private enum class ProfileArgKey {
        ID
    }

    private lateinit var viewModel: ProfileViewModel
    private var items: ArrayList<Post> = ArrayList()

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

        arguments?.let {bundle ->
            val id = bundle.getString(ProfileArgKey.ID.name) ?: return@let
            mId = id
            getUserData(id)
        } ?: backToPrevFragment()
        binding.swipeRefreshLayout.setOnRefreshListener {
            val id = viewModel.user.value?.id ?: return@setOnRefreshListener
            getUserData(mId)
        }

        toolbarSetup(binding.appBar, binding.swipeRefreshLayout)
        setupList(binding.list)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<View>(android.R.id.content)?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        activity?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }

    }

    private fun getUserData(id: String) {
        pnut.getUser(id).then { viewModel.user.postValue(it.data) }
        pnut.getPosts(id).then {
            viewModel.posts.postValue(ArrayList(it.data))
            binding.swipeRefreshLayout.isRefreshing = false
        }
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

    private fun setupList(list: RecyclerView) {
        items = viewModel.posts.value!!
        val adapter = PostItemRecyclerViewAdapter(context!!, items,
            object : PostItemFragment.OnListFragmentInteractionListener {
                override fun onListFragmentInteraction(item: Post?) {
                }
            }
        )
        val linear = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(context, linear.orientation)
        val divider = resources.getDrawable(R.drawable.post_list_divider, context?.theme)
        dividerItemDecoration.setDrawable(divider)
        list.addItemDecoration(dividerItemDecoration)
        list.layoutManager = linear
        list.adapter = adapter

        viewModel.posts.observe(this, Observer {
            items.addAll(it)
            adapter.notifyDataSetChanged()
        })
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
        when(it) {
            Event.FOLLOWING_LIST -> openFollowingList()
            Event.FOLLOWER_LIST -> openFollowerList()
        }
    }

    class ProfileViewModel : Store<Event>() {
        val user = MutableLiveData<User>()
        val posts = MutableLiveData<ArrayList<Post>>().apply { value = ArrayList() }
        val usernameWithAt: LiveData<String> = Transformations.map(user) { "@${it.username}"}
        val since: LiveData<CharSequence> = Transformations.map(user) {
            val calendar = Calendar.getInstance()
            calendar.time = it.createdAt
            DateFormat.format("yyyy/MM/dd", calendar)
        }
        val relation: LiveData<Int> = Transformations.map(user) {
            if(it.youFollow && it.followsYou && !it.youCanFollow) {
                // it's me!
                R.string.its_me
            } else if(it.followsYou) {
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

