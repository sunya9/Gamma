package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_user_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.model.io.GetUsersInputData
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.model.params.single.SearchUserParam
import net.unsweets.gamma.domain.usecases.GetUsersUseCase
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.EntityOnTouchListener
import net.unsweets.gamma.presentation.util.GlideApp
import javax.inject.Inject

abstract class UserListFragment : BaseListFragment<User, UserListFragment.UserViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<User, UserListFragment.UserViewHolder> {
    override val itemNameRes: Int = R.string.users
    override val baseListListener: BaseListRecyclerViewAdapter.IBaseList<User, UserViewHolder> by lazy {
        this
    }
    override val viewModel: BaseListViewModel<User> by lazy {
        ViewModelProviders.of(this, UserListViewModel.Factory(userListType, getUsersUseCase))
            .get(UserListViewModel::class.java)
    }


    @Inject
    lateinit var getUsersUseCase: GetUsersUseCase
    abstract val userListType: UserListType

    private val entityListener: View.OnTouchListener = EntityOnTouchListener()

    override fun createViewHolder(mView: View, viewType: Int): UserViewHolder = UserViewHolder(mView)

    override fun onClickItemListener(item: User) {
        val fragment = ProfileFragment.newInstance(item.id, item.content.avatarImage.link, item)
        addFragment(fragment, item.id)
    }

    override fun onBindViewHolder(item: User, viewHolder: UserViewHolder, position: Int, isMainItem: Boolean) {
        GlideApp.with(this).load(item.content.avatarImage.link).into(viewHolder.avatarView)
        viewHolder.screenNameTextView.text = item.username
        viewHolder.handleNameTextView.text = item.name
        viewHolder.bodyTextView.apply {
            text = item.content.getSpannableStringBuilder(viewHolder.itemView.context)
            setOnTouchListener(entityListener)
        }
    }

    override fun getItemLayout(): Int = R.layout.fragment_user_item

    class UserViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val avatarView: CircleImageView = itemView.avatarImageView
        val screenNameTextView: TextView = itemView.screenNameTextView
        val handleNameTextView: TextView = itemView.handleNameTextView
        val bodyTextView: TextView = itemView.bodyTextView
    }

    class UserListViewModel(
        private val userListType: UserListType,
        private val getUsersUseCase: GetUsersUseCase
    ) : BaseListViewModel<User>() {
        override suspend fun getItems(params: PaginationParam): PnutResponse<List<User>> {
            val getUsersParam = GetUsersParam(params.toMap()).apply { add(params) }
            if (userListType is UserListType.Search) getUsersParam.add(SearchUserParam(userListType.keyword))
            val getUsersInputData = GetUsersInputData(userListType, getUsersParam)
            return getUsersUseCase.run(getUsersInputData).res
        }

        class Factory(
            private val userListType: UserListType,
            private val getUsersUseCase: GetUsersUseCase
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserListViewModel(userListType, getUsersUseCase) as T
            }
        }

    }


    class SearchUserListFragment : UserListFragment() {
        override val userListType by lazy {
            UserListType.Search(keyword)
        }

        private val keyword by lazy {
            arguments?.getString(BundleKey.Keyword.name, "").orEmpty()
        }


        private enum class BundleKey { Keyword }
        companion object {
            fun newInstance(keyword: String) = SearchUserListFragment().apply {
                arguments = Bundle().apply {
                    putString(BundleKey.Keyword.name, keyword)
                }
            }
        }
    }
}
