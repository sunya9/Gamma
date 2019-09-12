package net.unsweets.gamma.presentation.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(private val threshold: Int, private val listener: Callback) :
    RecyclerView.OnScrollListener() {
    interface Callback {
        fun onLoadMore()
        val initialized: Boolean
    }
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (threshold == 0) return
        val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        if (totalItemCount <= (lastVisibleItem + threshold) && listener.initialized) {
            listener.onLoadMore()
        }
    }
}