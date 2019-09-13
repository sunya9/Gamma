package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_user_item.view.*
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.model.io.CacheUserInputData
import net.unsweets.gamma.domain.model.io.GetCachedUserListInputData
import net.unsweets.gamma.domain.model.io.GetUsersInputData
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.model.params.single.SearchUserParam
import net.unsweets.gamma.domain.model.preference.ShapeOfAvatar
import net.unsweets.gamma.domain.usecases.CacheUserUseCase
import net.unsweets.gamma.domain.usecases.GetCachedUserListUseCase
import net.unsweets.gamma.domain.usecases.GetUsersUseCase
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.EntityOnTouchListener
import net.unsweets.gamma.presentation.util.GlideApp
import javax.inject.Inject

abstract class UserListFragment : BaseListFragment<User, UserListFragment.UserViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<User, UserListFragment.UserViewHolder> {
    override val itemNameRes: Int = R.string.users
    override fun onClickSegmentListener(
        viewHolder: BaseListRecyclerViewAdapter.SegmentViewHolder,
        itemWrapper: PageableItemWrapper.Pager<User>
    ) {
        viewModel.loadMoreItems()
    }

    override val baseListListener: BaseListRecyclerViewAdapter.IBaseList<User, UserViewHolder> by lazy {
        this
    }
    override val viewModel: BaseListViewModel<User> by lazy {
        ViewModelProvider(
            this,
            UserListViewModel.Factory(
                userListType,
                getUsersUseCase,
                cachedUserListUseCase,
                cacheUserUseCase
            )
        )[UserListViewModel::class.java]
    }


    @Inject
    lateinit var getUsersUseCase: GetUsersUseCase
    abstract val userListType: UserListType
    @Inject
    lateinit var cachedUserListUseCase: GetCachedUserListUseCase
    @Inject
    lateinit var cacheUserUseCase: CacheUserUseCase

    private val entityListener: View.OnTouchListener = EntityOnTouchListener()

    override fun createViewHolder(mView: View, viewType: Int): UserViewHolder =
        UserViewHolder(mView, preferenceRepository.shapeOfAvatar)

    override fun onClickItemListener(
        viewHolder: UserViewHolder,
        item: User,
        itemWrapper: PageableItemWrapper<User>
    ) {
        val fragment = ProfileFragment.newInstance(item.id, item.content.avatarImage.link, item)
        addFragment(fragment, item.id)
    }

    override fun onBindViewHolder(
        item: User,
        viewHolder: UserViewHolder,
        position: Int,
        isMainItem: Boolean
    ) {
        GlideApp.with(this).load(item.content.avatarImage.link).into(viewHolder.avatarView)
        viewHolder.screenNameTextView.text = item.username
        viewHolder.handleNameTextView.text = item.name
        viewHolder.bodyTextView.apply {
            text = item.content.getSpannableStringBuilder(viewHolder.itemView.context)
            setOnTouchListener(entityListener)
        }
        viewHolder.relationshipTextView.visibility = getVisibility(!item.me && item.followsYou)
    }

    private fun getVisibility(b: Boolean) = if (b) View.VISIBLE else View.GONE

    override fun getItemLayout(): Int = R.layout.fragment_user_item

    class UserViewHolder(mView: View, shapeOfAvatar: ShapeOfAvatar) :
        RecyclerView.ViewHolder(mView) {
        val avatarView: ImageView = itemView.avatarImageView.also {
            it.setBackgroundResource(shapeOfAvatar.drawableRes)
        }
        val screenNameTextView: TextView = itemView.screenNameTextView
        val handleNameTextView: TextView = itemView.handleNameTextView
        val bodyTextView: TextView = itemView.bodyTextView
        val relationshipTextView: TextView = itemView.relationshipTextView
    }

    class UserListViewModel(
        private val userListType: UserListType,
        private val getUsersUseCase: GetUsersUseCase,
        private val cachedUserListUseCase: GetCachedUserListUseCase,
        private val cacheUserUseCase: CacheUserUseCase
    ) : BaseListViewModel<User>() {
        override suspend fun getItems(requestPager: PageableItemWrapper.Pager<User>?): PnutResponse<List<User>> {
            val getUsersParam = GetUsersParam().apply {
                requestPager?.let { add(PaginationParam.createFromPager(it)) }
            }
            if (userListType is UserListType.Search) getUsersParam.add(SearchUserParam(userListType.keyword))
            val getUsersInputData = GetUsersInputData(userListType, getUsersParam)
            return getUsersUseCase.run(getUsersInputData).res
        }

        override fun loadCache() {
            viewModelScope.launch {
                val res = cachedUserListUseCase.run(GetCachedUserListInputData((userListType)))
                items.addAll(res.users.data)
                super.loadCache()
            }
        }

        override fun storeItems() {
            viewModelScope.launch {
                runCatching {
                    cacheUserUseCase.run(CacheUserInputData(items, userListType))
                }
            }
        }

        class Factory(
            private val userListType: UserListType,
            private val getUsersUseCase: GetUsersUseCase,
            private val cachedUserListUseCase: GetCachedUserListUseCase,
            private val cacheUserUseCase: CacheUserUseCase

        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return UserListViewModel(
                    userListType,
                    getUsersUseCase,
                    cachedUserListUseCase,
                    cacheUserUseCase
                ) as T
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
