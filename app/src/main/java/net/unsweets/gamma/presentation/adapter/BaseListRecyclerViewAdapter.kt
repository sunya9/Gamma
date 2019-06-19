package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import net.unsweets.gamma.domain.entity.Unique

class BaseListRecyclerViewAdapter<T : Unique, V : RecyclerView.ViewHolder>(
    private val listLiveData: LiveData<ArrayList<T>>,
    private var listener: IBaseList<T, V>
) : RecyclerView.Adapter<V>() {
    override fun getItemCount(): Int = listLiveData.value?.size ?: 0

    var recyclerView: RecyclerView? = null

    private var skeleton: RecyclerViewSkeletonScreen? = null

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemId(position: Int): Long {
        return listLiveData.value?.get(position)?.uniqueKey?.toLong() ?: 0L
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        val view = LayoutInflater.from(parent.context)
            .inflate(listener.getItemLayout(), parent, false)
        return listener.createViewHolder(view, viewType)
    }

    interface IBaseList<T, V> {
        fun createViewHolder(mView: View, viewType: Int = 0): V
        fun onClickItemListener(item: T)
        fun onBindViewHolder(item: T, viewHolder: V, position: Int)
        fun getItemLayout(): Int
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = listLiveData.value?.get(position) ?: return
        listener.onBindViewHolder(item, holder, position)
        holder.itemView.setOnClickListener { listener.onClickItemListener(item) }

    }
}
