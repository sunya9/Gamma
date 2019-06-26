package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
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
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.InfiniteScrollListener
import net.unsweets.gamma.presentation.util.SmoothScroller
import net.unsweets.gamma.presentation.view.DividerIgnoreLastItem
import net.unsweets.gamma.util.SingleLiveEvent


abstract class BaseListFragment<T, V : RecyclerView.ViewHolder> : BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener,
    InfiniteScrollListener.Callback where T : Unique, T : Parcelable, T : Pageable {

    private val listEventObserver = Observer<ListEvent> {
        @Suppress("UNCHECKED_CAST")
        when (it) {
            is ListEvent.ReceiveNewItems<*> -> receiveNewItems(it.items as List<T>, it.insertPosition)
        }
    }

    @DrawableRes
    open val dividerDrawable: Int = R.drawable.divider_inset

    private val infiniteScrollListener by lazy {
        InfiniteScrollListener(this)
    }

    private fun receiveNewItems(items: List<T>, insertPosition: Int?) {
        val currentItems = viewModel.items.value ?: ArrayList()
        val insertPos = insertPosition ?: 0
        viewModel.items.value = currentItems.also { it.addAll(insertPos, items) }
        adapter.notifyItemRangeInserted(insertPos, items.size)
        adapter.updateFooter()
        viewModel.loading.postValue(false)

        getSwipeRefreshLayout(view ?: return).isRefreshing = false
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    protected val adapter: BaseListRecyclerViewAdapter<T, V> by lazy {
        BaseListRecyclerViewAdapter(viewModel.items, viewModel.olderDirectionMeta, baseListListener)
    }
    abstract val viewModel: BaseListViewModel<T>

    fun scrollToTop() {
        val recyclerView = getRecyclerView(view ?: return)
        recyclerView.layoutManager?.startSmoothScroll(SmoothScroller(recyclerView.context))
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
        val dividerItemDecoration = DividerIgnoreLastItem(context, DividerIgnoreLastItem.VERTICAL)
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
        class ReceiveNewItems<T>(val items: List<T>, val insertPosition: Int? = 0) : ListEvent()
        class Failure(val t: Throwable) : ListEvent()
    }

    abstract class BaseListViewModel<T> : ViewModel() where T : Pageable, T : Unique {
        val items = MutableLiveData<ArrayList<T>>().apply { value = ArrayList() }
        val loading = MutableLiveData<Boolean>().apply { value = false }
        val olderDirectionMeta = MutableLiveData<PnutResponse.Meta>()
        val newerDirectionMeta = MutableLiveData<PnutResponse.Meta>()
        private var lastPagination: PaginationParam? = null
        val listEvent = SingleLiveEvent<ListEvent>()

        init {
            loadItems()
        }

        fun loadNewItems() {
            if (loading.value == true) return
            val pagination = items.value?.firstOrNull()?.let {
                PaginationParam(maxId = it.paginationId)
            } ?: PaginationParam()
            loadItems(pagination)
        }

        fun loadMoreItems() {
            if (loading.value == true) return

            val pagination = items.value?.lastOrNull()?.let {
                PaginationParam(minId = it.paginationId)
            } ?: PaginationParam()
            Log.e("pagination", pagination.toString())
            if (lastPagination == pagination) return
            lastPagination = pagination
            loadItems(pagination)
        }

        abstract suspend fun getItems(params: PaginationParam): PnutResponse<List<T>>
        private fun loadItems(pagination: PaginationParam = PaginationParam()) {
            // prevent to send same request multi time
            if (loading.value == true) return
            loading.value = true
            viewModelScope.launch {
                runCatching {
                    getItems(pagination)
                }.onSuccess {
                    val insertPosition = if (!pagination.maxId.isNullOrEmpty()) 0 else items.value?.size
                    Log.e("insertPosition", insertPosition.toString())
                    when {
                        !pagination.minId.isNullOrEmpty() -> {
                            olderDirectionMeta.value = it.meta
                        }
                        !pagination.maxId.isNullOrEmpty() -> {
                            newerDirectionMeta.value = it.meta
                        }
                    }
                    listEvent.value = ListEvent.ReceiveNewItems(it.data, insertPosition)
                }.onFailure {
                    Log.e("error", it.message ?: "no message")
                    listEvent.value = ListEvent.Failure(it)
                    lastPagination = null
                }
            }
        }
    }

    protected open fun getSwipeRefreshLayout(view: View): SwipeRefreshLayout = view.swipeRefreshLayout

    abstract val baseListListener: BaseListRecyclerViewAdapter.IBaseList<T, V>
}