package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
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
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.UniquePageable
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.InfiniteScrollListener
import net.unsweets.gamma.presentation.util.SmoothScroller
import net.unsweets.gamma.presentation.view.DividerIgnoreLastItem
import net.unsweets.gamma.util.ErrorIntent
import net.unsweets.gamma.util.LogUtil
import net.unsweets.gamma.util.SingleLiveEvent


abstract class BaseListFragment<T : UniquePageable, V : RecyclerView.ViewHolder> : BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener,
    InfiniteScrollListener.Callback {
    open val reverse = false

    private val listEventObserver = Observer<ListEvent> {
        //        LogUtil.e("listEventObserver $it")
        @Suppress("UNCHECKED_CAST")
        when (it) {
            is ListEvent.ReceiveNewItems<*> -> receiveNewItems(
                it.response as PnutResponse<List<T>>,
                it.requestPager as PageableItemWrapper<T>?
            )
            is ListEvent.Failure -> failure(it.t)
            is ListEvent.Initialized -> initialized()
        }
    }

    private fun initialized() {
        viewModel.initialized = true
        adapter.notifyDataSetChanged()
        viewModel.loadNewItems()
        viewModel.loadMoreItems()
    }

    private fun failure(t: Throwable) {
        ErrorIntent.broadcast(context ?: return, t)
        adapter.showRetryMessage()
    }

    override val initialized: Boolean
        get() = viewModel.initialized
    @DrawableRes
    open val dividerDrawable: Int = R.drawable.divider_inset

    private val infiniteScrollListener by lazy {
        InfiniteScrollListener(preferenceRepository.thresholdOfAutoPager, this)
    }


    @Synchronized
    private fun receiveNewItems(
        response: PnutResponse<List<T>>,
        requestPager: PageableItemWrapper<T>?
    ) {
        LogUtil.e("receiveNewItems ${response.data.size}")
        val insertIndex = adapter.removeSegmentIfNeed(requestPager)
        adapter.insertItems(requestPager, response, insertIndex)
        viewModel.loading.postValue(false)
        getSwipeRefreshLayout(view ?: return).isRefreshing = false
        if (preferenceRepository.cache) viewModel.storeItems()
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
        if (savedInstanceState == null) {
            if (preferenceRepository.cache) viewModel.loadCache()
            else viewModel.listEvent.postValue(ListEvent.Initialized)
        }
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
        data class ReceiveNewItems<T : UniquePageable>(
            val response: PnutResponse<List<T>>,
            val requestPager: PageableItemWrapper<T>?
        ) :
            ListEvent()

        data class Failure(val t: Throwable) : ListEvent()
        object Initialized : ListEvent()
    }

    abstract class BaseListViewModel<T : UniquePageable> : ViewModel() {
        var initialized = false
        val items = ArrayList<PageableItemWrapper<T>>()
        val loading = MutableLiveData<Boolean>().apply { value = false }
        private var lastPagination: PaginationParam? = null
        val listEvent = SingleLiveEvent<ListEvent>()

        fun loadNewItems() {
            if (loading.value == true) return
            val pager = PageableItemWrapper.Pager<T>(
                minId = items.firstOrNull()?.getSinceId(),
                virtual = true
            )
            loadItems(pager)
        }

        fun loadMoreItems() {
            if (loading.value == true) return
            val lastItem = items.lastOrNull()
            if (lastItem !is PageableItemWrapper.Pager) return
            if (!lastItem.more) return
            loadItems(lastItem)
        }

        abstract suspend fun getItems(requestPager: PageableItemWrapper.Pager<T>?): PnutResponse<List<T>>
        fun loadSegmentItems(requestPager: PageableItemWrapper.Pager<T>) {
            loadItems(requestPager)
        }

        private fun loadItems(requestPager: PageableItemWrapper.Pager<T>?) {
            // prevent to send same request multi time
            if (loading.value == true) return
            loading.value = true
            LogUtil.e("requestPager $requestPager")
            viewModelScope.launch {
                runCatching {
                    getItems(requestPager)
                }.onSuccess {
                    listEvent.postValue(ListEvent.ReceiveNewItems(it, requestPager))
                }.onFailure {
                    LogUtil.e(it.message ?: "no message")
                    listEvent.postValue(ListEvent.Failure(it))
                    lastPagination = null
                }
            }
        }

        open fun loadCache() {
            listEvent.postValue(ListEvent.Initialized)
        }

        open fun storeItems() {
        }
    }

    protected open fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout =
        view.swipeRefreshLayout

    abstract val baseListListener: BaseListRecyclerViewAdapter.IBaseList<T, V>
}