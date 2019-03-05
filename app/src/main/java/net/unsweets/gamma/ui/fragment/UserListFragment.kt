package net.unsweets.gamma.ui.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_user_item.view.*
import kotlinx.android.synthetic.main.fragment_user_list.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentUserListBinding
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.model.net.PnutRepository.UserListMode
import net.unsweets.gamma.ui.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.ui.base.BaseViewModel
import net.unsweets.gamma.ui.util.ComputedLiveData
import net.unsweets.gamma.ui.util.EntityOnTouchListener
import net.unsweets.gamma.ui.util.FragmentHelper
import net.unsweets.gamma.ui.util.GlideApp

class UserListFragment : BaseListFragment<User, UserListFragment.UserViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<User, UserListFragment.UserViewHolder> {
    private enum class BundleKey {
        Mode, ID, Count, Username
    }

    private lateinit var id: String

    private lateinit var viewModel: UserListViewModel
    private val entityListener: View.OnTouchListener = EntityOnTouchListener()
    override val diffCallback: DiffUtil.ItemCallback<User> = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }

    private val usersObserver = Observer<PagedList<User>> { adapter.submitList(it) }
    private val eventObserver = Observer<Event> {
        when (it) {
            Event.Back -> FragmentHelper.backFragment(fragmentManager)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        super.onCreate(savedInstanceState)
        if (!isConfigurationChanges) {
            arguments?.let { bundle ->
                // mode
                val mode = bundle.getSerializable(BundleKey.Mode.name) as UserListMode
                id = bundle.getString(BundleKey.ID.name) ?: return@let
                viewModel.init(id, mode)
                viewModel.mode.value = mode

                // count
                val count = bundle.getInt(BundleKey.Count.name)
                viewModel.count.value = count

                // username
                val username = bundle.getString(BundleKey.Username.name, "")
                viewModel.username.value = username

            }
        }
        viewModel.users.observe(this, usersObserver)
        viewModel.event.observe(this, eventObserver)
    }

    override fun getBaseListListener(): BaseListRecyclerViewAdapter.IBaseList<User, UserViewHolder> = this

    override fun createViewHolder(mView: View): UserViewHolder = UserViewHolder(mView)

    override fun onClickItemListener(item: User) {
        val fragment = ProfileFragment.newInstance(item.id, item.content.avatarImage.link, item)
        addFragment(fragment, item.id)
    }

    override fun onBindViewHolder(item: User, viewHolder: UserViewHolder, position: Int) {
        GlideApp.with(this).load(item.content.avatarImage.link).into(viewHolder.avatarView)
        viewHolder.screenNameTextView.text = item.username
        viewHolder.handleNameTextView.text = item.name
        viewHolder.bodyTextView.apply {
            text = item.content.getSpannableStringBuilder(viewHolder.itemView.context)
            setOnTouchListener(entityListener)
        }
    }

    override fun getItemLayout(): Int = R.layout.fragment_user_item
    override fun getRecyclerView(view: View): RecyclerView = view.userList

    private lateinit var binding: FragmentUserListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onRefreshed() {
    }

    class UserViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val avatarView: CircleImageView = itemView.avatarImageView
        val screenNameTextView: TextView = itemView.screenNameTextView
        val handleNameTextView: TextView = itemView.handleNameTextView
        val bodyTextView: TextView = itemView.bodyTextView
    }

    enum class Event { Back }

    class UserListViewModel(app: Application) : BaseViewModel<Event>(app) {
        lateinit var users: LiveData<PagedList<User>>
        val mode = MutableLiveData<UserListMode>()
        fun init(id: String, mode: UserListMode) {
            users = pnutRepository.getUsers(id, mode)
        }

        val username = MutableLiveData<String>().apply { value = "" }
        val count = MutableLiveData<Int>().apply { value = 0 }
        val title = ComputedLiveData.of(mode, username) { mode, username ->
            when {
                mode != null && username != null -> {
                    @StringRes val res = when (mode) {
                        UserListMode.Following -> R.string.following_with_name
                        UserListMode.Followers -> R.string.followers_with_name
                    }
                    app.getString(res, username)
                }
                else -> ""
            }
        }
        val subtitle = Transformations.map(count) {
            app.resources.getQuantityString(R.plurals.user, it, it)
        }

        fun back() = sendEvent(Event.Back)
    }

    companion object {
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
                    putSerializable(BundleKey.Mode.name, userListMode)
                    putString(BundleKey.ID.name, id)
                    putString(BundleKey.Username.name, username)
                    putInt(BundleKey.Count.name, count)
                }
            }
        }
    }

}
