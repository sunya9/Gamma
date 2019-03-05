package net.unsweets.gamma.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen

class BaseListRecyclerViewAdapter<T, V : RecyclerView.ViewHolder>(
    private var listener: IBaseList<T, V>,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, V>(diffCallback) {
    var recyclerView: RecyclerView? = null

    private var skeleton: RecyclerViewSkeletonScreen? = null

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        val view = LayoutInflater.from(parent.context)
            .inflate(listener.getItemLayout(), parent, false)
        return listener.createViewHolder(view)
    }

    interface IBaseList<T, V> {
        fun createViewHolder(mView: View): V
        fun onClickItemListener(item: T)
        fun onBindViewHolder(item: T, viewHolder: V, position: Int)
        fun getItemLayout(): Int
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = getItem(position) ?: return
        listener.onBindViewHolder(item, holder, position)
        holder.itemView.setOnClickListener { listener.onClickItemListener(item) }

    }

}
