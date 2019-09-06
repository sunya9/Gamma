package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_base_list.view.*
import kotlinx.coroutines.launch
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Pageable
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Unique
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.InfiniteScrollListener
import net.unsweets.gamma.presentation.util.SmoothScroller
import net.unsweets.gamma.presentation.view.DividerIgnoreLastItem
import net.unsweets.gamma.util.ErrorIntent
import net.unsweets.gamma.util.LogUtil
import net.unsweets.gamma.util.SingleLiveEvent


abstract class BaseListFragment<T, V : RecyclerView.ViewHolder> : BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener,
    InfiniteScrollListener.Callback where T : Unique, T : Parcelable, T : Pageable {
    open val reverse = false

    private val listEventObserver = Observer<ListEvent> {
        @Suppress("UNCHECKED_CAST")
        when (it) {
            is ListEvent.ReceiveNewItems<*> -> receiveNewItems(
                it.response as PnutResponse<List<T>>,
                it.pager as PageableItemWrapper.Pager<T>,
                it.insertPosition
            )
            is ListEvent.Failure -> failure(it.t)
//            is ListEvent.UpdateNewerItem -> updateNewerItem(it.meta)
//            is ListEvent.UpdateOlderItem -> updateOlderItem(it.meta)
        }
    }

    private fun updateOlderItem(meta: PnutResponse.Meta) {
//        adapter.addItem()
        viewModel.items.add(PageableItemWrapper.Pager.createFromMeta(meta))
    }

    private fun updateNewerItem(meta: PnutResponse.Meta) {
        viewModel.items.add(0, PageableItemWrapper.Pager.createFromMeta(meta))
    }

    private fun failure(t: Throwable) {
        ErrorIntent.broadcast(context ?: return, t)
        adapter.showRetryMessage()
    }

    @DrawableRes
    open val dividerDrawable: Int = R.drawable.divider_inset

    private val infiniteScrollListener by lazy {
        InfiniteScrollListener(this)
    }

    @Synchronized
    private fun receiveNewItems(
        response: PnutResponse<List<T>>,
        pager: PageableItemWrapper.Pager<T>,
        insertPosition: Int?
    ) {
        val items = response.data
        val currentItems = viewModel.items
        val insertPos = insertPosition ?: 0
        val isFirstTime = insertPos == 0 && currentItems.size == 1
        val pageableItemWrapperItems = items.map { PageableItemWrapper.Item(it) }
        viewModel.items.addAll(insertPos, pageableItemWrapperItems)
        // scroll to top when insert in first time
        if (isFirstTime)
            adapter.notifyItemRangeChanged(insertPos, items.size)
        else
            adapter.notifyItemRangeInserted(insertPos, items.size)
        adapter.updateSegment(pager, response.meta)
        LogUtil.e("insertPosition: $insertPos")
        viewModel.loading.postValue(false)

        getSwipeRefreshLayout(view ?: return).isRefreshing = false
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    protected val adapter: BaseListRecyclerViewAdapter<T, V> by lazy {
        BaseListRecyclerViewAdapter(overrideOptions(defaultOptions))
    }

    private val defaultOptions by lazy {
        BaseListRecyclerViewAdapter.BaseListRecyclerViewAdapterOptions(
            viewModel.items, baseListListener, reverse
        )
    }

    open fun overrideOptions(
        options: BaseListRecyclerViewAdapter.BaseListRecyclerViewAdapterOptions<T, V>
    ): BaseListRecyclerViewAdapter.BaseListRecyclerViewAdapterOptions<T, V> {
        return options
    }

    abstract val viewModel: BaseListViewModel<T>

    fun scrollToTop() {
        val recyclerView = getRecyclerView(view ?: return)
        context?.let { recyclerView.layoutManager?.startSmoothScroll(SmoothScroller(it)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.listEvent.observe(this, listEventObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getFragmentLayout(), container, false)
    }

    protected open fun getFragmentLayout(): Int = R.layout.fragment_base_list
    protected open fun getRecyclerView(view: View): RecyclerView = view.baseList


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(getRecyclerView(view))
        getSwipeRefreshLayout(view).setOnRefreshListener(this)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        val dividerItemDecoration =
            DividerIgnoreLastItem(context, DividerIgnoreLastItem.VERTICAL, reverse)
        val divider = AppCompatResources.getDrawable(context!!, dividerDrawable)!!
        dividerItemDecoration.setDrawable(divider)
        recyclerView.addItemDecoration(dividerItemDecoration)
        recyclerView.addOnScrollListener(infiniteScrollListener)
    }

    override fun onDestroyView() {
        getRecyclerView(view!!).removeOnScrollListener(infiniteScrollListener)
        super.onDestroyView()
    }

    override fun onRefresh() {
        viewModel.loadNewItems()
    }

    override fun onLoadMore() {
        viewModel.loadMoreItems()
    }

    sealed class ListEvent {
        data class ReceiveNewItems<T>(
            val response: PnutResponse<List<T>>,
            val pager: PageableItemWrapper.Pager<T>,
            val insertPosition: Int? = 0
        ) :
            ListEvent() where T : Unique, T : Pageable

        data class Failure(val t: Throwable) : ListEvent()
//        data class UpdateOlderItem(val meta: PnutResponse.Meta) : ListEvent()
//        data class UpdateNewerItem(val meta: PnutResponse.Meta) : ListEvent()
    }


    abstract class BaseListViewModel<T> : ViewModel() where T : Pageable, T : Unique {
        val items = ArrayList<PageableItemWrapper<T>>()
        val loading = MutableLiveData<Boolean>().apply { value = false }
        private var lastPagination: PaginationParam? = null
        val listEvent = SingleLiveEvent<ListEvent>()

        fun loadNewItems() {
            if (loading.value == true) return
            val firstItem = items.firstOrNull()
            val pagination = firstItem?.let {
                PaginationParam(maxId = it.getSinceId())
            } ?: PaginationParam()
            loadItems(pagination)
        }

        fun loadMoreItems() {
            if (loading.value == true) return
            val lastItem = items.lastOrNull()
            if (lastItem is PageableItemWrapper.Pager && !lastItem.more) return
            val pagination = lastItem?.let {
                PaginationParam(minId = it.getBeforeId())
            } ?: PaginationParam()
            LogUtil.e(pagination.toString())
            if (lastPagination == pagination) return
            lastPagination = pagination
            loadItems(pagination)
        }

        private fun fallbackInsertPosition(): Int {
            val footerOnly = items.size == 1
            return if (footerOnly) 0 else items.size - 1
        }

        abstract suspend fun getItems(params: PaginationParam): PnutResponse<List<T>>
        private fun loadItems(pagination: PaginationParam = PaginationParam()) {
            // prevent to send same request multi time
            if (loading.value == true) return
            loading.value = true
            val requestPager = PageableItemWrapper.Pager<T>(
                maxId = pagination.maxId,
                minId = pagination.minId
            )
            LogUtil.e("requestPager  $requestPager ")
            viewModelScope.launch {
                runCatching {
                    getItems(pagination)
                }.onSuccess {
                    val insertPosition =
                        if (!pagination.maxId.isNullOrEmpty()) 0 else fallbackInsertPosition()
                    LogUtil.e(insertPosition.toString())
                    when {
                        !pagination.minId.isNullOrEmpty() -> {
//                            listEvent.emit(ListEvent.UpdateOlderItem(it.meta))
                        }
                        !pagination.maxId.isNullOrEmpty() -> {
//                            listEvent.emit(ListEvent.UpdateNewerItem(it.meta))
                        }
                    }
                    listEvent.postValue(ListEvent.ReceiveNewItems(it, requestPager, insertPosition))
                }.onFailure {
                    LogUtil.e(it.message ?: "no message")
                    listEvent.postValue(ListEvent.Failure(it))
                    lastPagination = null
                }
            }
        }
    }

    protected open fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout =
        view.swipeRefreshLayout

    abstract val baseListListener: BaseListRecyclerViewAdapter.IBaseList<T, V>
}