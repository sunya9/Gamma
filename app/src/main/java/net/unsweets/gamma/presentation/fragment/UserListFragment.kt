package net.unsweets.gamma.presentation.fragment

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_user_item.view.*
import kotlinx.android.synthetic.main.fragment_user_list.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentUserListBinding
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.model.io.GetUsersInputData
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.usecases.GetUsersUseCase
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.EntityOnTouchListener
import net.unsweets.gamma.presentation.util.FragmentHelper
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.util.SingleLiveEvent
import javax.inject.Inject

abstract class UserListFragment : NewBaseListFragment<User, UserListFragment.UserViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<User, UserListFragment.UserViewHolder> {
    override val baseListListener: BaseListRecyclerViewAdapter.IBaseList<User, UserViewHolder> = this

    private enum class BundleKey {
        User
    }

    private val user: User by lazy { arguments!!.getParcelable<User>(BundleKey.User.name) }
    @Inject
    lateinit var getUsersUseCase: GetUsersUseCase
    abstract val userListType: UserListType
    override val viewModel: UserListViewModel by lazy {
        ViewModelProviders.of(
            this,
            UserListViewModel.Factory(activity!!.application, user, userListType, getUsersUseCase)
        ).get(UserListViewModel::class.java)


    }
    private val entityListener: View.OnTouchListener = EntityOnTouchListener()

    private val eventObserver = Observer<Event> {
        when (it) {
            is Event.Back -> FragmentHelper.backFragment(fragmentManager)
        }
    }

    override fun createViewHolder(mView: View, viewType: Int): UserViewHolder = UserViewHolder(mView)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.event
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    class UserViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val avatarView: CircleImageView = itemView.avatarImageView
        val screenNameTextView: TextView = itemView.screenNameTextView
        val handleNameTextView: TextView = itemView.handleNameTextView
        val bodyTextView: TextView = itemView.bodyTextView
    }

    sealed class Event {
        object Back : Event()
    }

    class UserListViewModel(
        private val app: Application,
        private val user: User,
        private val userListType: UserListType,
        private val getUsersUseCase: GetUsersUseCase
    ) : BaseListViewModel<User>() {
        val event = SingleLiveEvent<Event>()
        override suspend fun getItems(params: PaginationParam): PnutResponse<List<User>> {
            val getUsersParam = GetUsersParam().apply { add(params) }
            val getUsersInputData = GetUsersInputData(user.id, userListType, getUsersParam)
            return getUsersUseCase.run(getUsersInputData).res
        }
        private val count = when(userListType) {
            is UserListType.Following -> user.counts.following
            is UserListType.Followers -> user.counts.followers
        }
        val title = {
            @StringRes val res = when (userListType) {
                UserListType.Following -> R.string.following_with_name
                UserListType.Followers -> R.string.followers_with_name
            }
            app.getString(res, user.username)
        }
        val subtitle  = app.resources.getQuantityString(R.plurals.user, count, count)

        fun back() {
            event.value = Event.Back
        }
        class Factory(
            private val application: Application,
            private val user: User,
            private val userListType: UserListType,
            private val getUsersUseCase: GetUsersUseCase
        ) : ViewModelProvider.AndroidViewModelFactory(application) {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserListViewModel(application, user, userListType, getUsersUseCase) as T
            }
        }

    }

    override fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout = view.userListRefreshLayout

    class FollowerListFragment: UserListFragment() {
        override val userListType =UserListType.Followers
        companion object {
            fun newInstance(user: User) = FollowerListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BundleKey.User.name, user)
                }
            }
        }
    }
    class FollowingListFragment: UserListFragment() {
        override val userListType =UserListType.Following
        companion object {
            fun newInstance(user: User) = FollowingListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BundleKey.User.name, user)
                }
            }
        }
    }
}
