package net.unsweets.gamma.presentation.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(val listener: Callback) : RecyclerView.OnScrollListener() {
    interface Callback {
        fun onLoadMore()
        val initialized: Boolean
    }
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val totalItemCount = linearLayoutManager.itemCount
        val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
        val visibleThreshold = 20
        if (totalItemCount <= (lastVisibleItem + visibleThreshold) && listener.initialized) {
            listener.onLoadMore()
        }
    }
}