package net.unsweets.gamma.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_user.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.api.PnutResponse
import net.unsweets.gamma.databinding.FragmentUserListBinding
import net.unsweets.gamma.model.User
import net.unsweets.gamma.util.Store
import retrofit2.Call

class UserListFragment : BaseListFragment<User, UserListFragment.UserViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<User, UserListFragment.UserViewHolder> {
    private enum class UserListMode {
        Following, Followers
    }

    private enum class BundleKey {
        Mode, ID, Count, Username
    }

    private lateinit var mode: UserListMode
    private lateinit var id: String

    private lateinit var viewModel: UserListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        arguments?.let { bundle ->
            // mode
            val modeOrdinal = bundle.getInt(BundleKey.Mode.name)
            id = bundle.getString(BundleKey.ID.name) ?: return@let
            mode = UserListMode.values()[modeOrdinal]

            // count
            val count = bundle.getInt(BundleKey.Count.name)
            viewModel.count.value = count

            // username
            val username = bundle.getString(BundleKey.Username.name, "")
            viewModel.username.value = username
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            DataBindingUtil.inflate<FragmentUserListBinding>(inflater, R.layout.fragment_user_list, container, false)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        binding.toolbar.let {
            it.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            it.setNavigationOnClickListener { activity?.onBackPressed() }
        }
//        return binding.root
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private var listener: OnListFragmentInteractionListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        }
    }

    override fun getBaseListListener(): BaseListRecyclerViewAdapter.IBaseList<User, UserViewHolder> = this

    override fun getItems(): Call<PnutResponse<List<User>>> = when (mode) {
        UserListMode.Followers -> pnut.getFollowers(id)
        UserListMode.Following -> pnut.getFollowing(id)
    }

    override fun getPaginateId(item: User): String = item.id

    override fun createViewHolder(mView: View): UserViewHolder = UserViewHolder(mView)

    override fun onClickItemListener(item: User?) {
        val fragment = ProfileFragment.newInstance(id)
        addFragment(fragment, null)
    }

    override fun onBindViewHolder(item: User, viewHolder: UserViewHolder, position: Int) {
        Glide.with(viewHolder.avatarView).load(item.content.avatarImage.link).into(viewHolder.avatarView)
        viewHolder.screenNameTextView.text = item.username
        viewHolder.bodyTextView.text = item.content.text
    }

    override fun getLayout(): Int = R.layout.fragment_user


    class UserViewHolder(mView: View) : BaseListRecyclerViewAdapter.BaseViewHolder(mView) {
        val avatarView: CircleImageView = mView.avatarImageView
        val screenNameTextView: TextView = mView.screenNameTextView
        val bodyTextView: TextView = mView.bodyTextView
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
        val count = MutableLiveData<Int>().apply { value = 0 }
    }

    companion object {
        @JvmStatic
        fun followerList(user: User) = newInstance(UserListMode.Followers, user)

        fun followingList(user: User) = newInstance(UserListMode.Following, user)
        private fun newInstance(userListMode: UserListMode, user: User): Fragment {
            val id = user.id
            val username = user.username
            val count = when (userListMode) {
                UserListMode.Following -> user.counts.following
                UserListMode.Followers -> user.counts.followers
            }
            return UserListFragment().apply {
                arguments = Bundle().apply {
                    putInt(BundleKey.Mode.name, userListMode.ordinal)
                    putString(BundleKey.ID.name, id)
                    putString(BundleKey.Username.name, username)
                    putInt(BundleKey.Count.name, count)
                }
            }
        }
    }
}
