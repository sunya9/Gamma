package net.unsweets.gamma.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import net.unsweets.gamma.R
import net.unsweets.gamma.adapter.UserRecyclerViewAdapter
import net.unsweets.gamma.databinding.FragmentUserListBinding
import net.unsweets.gamma.model.User
import net.unsweets.gamma.util.Store
import net.unsweets.gamma.util.then

class UserListFragment : BaseFragment() {
    enum class UserListMode {
        FOLLOWING, FOLLOWERS
    }

    private enum class ArgKey {
        MODE, ID, COUNT, USERNAME
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil.inflate<FragmentUserListBinding>(inflater, R.layout.fragment_user_list, container, false)
        binding.setLifecycleOwner(this)
        val viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        binding.viewModel = viewModel

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            it.setNavigationOnClickListener { backToPrevFragment() }
        }
        val view = binding.root
        val listView = binding.list
        // Set the adapter
        val users = ArrayList<User>()
        listView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = UserRecyclerViewAdapter(users, listener)
        }
        arguments?.let { bundle ->
            // mode
            val modeOrdinal = bundle.getInt(ArgKey.MODE.name)
            val id = bundle.getString(ArgKey.ID.name) ?: return@let
            val mode = UserListMode.values()[modeOrdinal]
            val call = when (mode) {
                UserListMode.FOLLOWERS -> pnut.getFollowers(id)
                UserListMode.FOLLOWING -> pnut.getFollowing(id)
            }
            call.then { viewModel.users.postValue(ArrayList(it.data)) }

            // count
            val count = bundle.getInt(ArgKey.COUNT.name)
            viewModel.count.value = count

            // username
            val username = bundle.getString(ArgKey.USERNAME.name, "")
            viewModel.username.value = username
        }
        return view
    }

    private var listener: OnListFragmentInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onClickUser(item: User)
    }

    class UserListViewModel : Store<UserListViewModel.Event>() {
        enum class Event

        val username = MutableLiveData<String>().apply { value = "" }
        val users = MutableLiveData<ArrayList<User>>().apply { value = ArrayList() }
        val count = MutableLiveData<Int>().apply { value = 0 }
    }

    companion object {
        @JvmStatic
        fun newInstance(userListMode: UserListMode, user: User): Fragment {
            val id = user.id
            val username = user.username
            val count = when (userListMode) {
                UserListMode.FOLLOWING -> user.counts.following
                UserListMode.FOLLOWERS -> user.counts.followers
            }
            return UserListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ArgKey.MODE.name, userListMode.ordinal)
                    putString(ArgKey.ID.name, id)
                    putString(ArgKey.USERNAME.name, username)
                    putInt(ArgKey.COUNT.name, count)
                }
            }
        }
    }
}
