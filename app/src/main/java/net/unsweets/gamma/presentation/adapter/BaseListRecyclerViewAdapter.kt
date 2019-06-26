package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import kotlinx.android.synthetic.main.footer_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Unique

class BaseListRecyclerViewAdapter<T : Unique, V : RecyclerView.ViewHolder>(
    private val listLiveData: LiveData<ArrayList<T>>,
    private val olderDirectionMeta: LiveData<PnutResponse.Meta>,
    private var listener: IBaseList<T, V>
) : RecyclerView.Adapter<V>() {
    // empty view; loading indicator;
    override fun getItemCount(): Int = bodyItemCount + 1

    private val bodyItemCount: Int
        get() = (listLiveData.value?.size ?: 0)

    var recyclerView: RecyclerView? = null

    private var skeleton: RecyclerViewSkeletonScreen? = null

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemId(position: Int): Long {
        return listLiveData.value?.get(position)?.uniqueKey?.toLong() ?: 0L
    }

    private enum class ViewType { Body, Footer }

    override fun getItemViewType(position: Int): Int {
        val isLastItem = itemCount == position + 1
        return when {
            isLastItem -> ViewType.Footer
            else -> ViewType.Body
        }.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        val inflater = LayoutInflater.from(parent.context)
        @Suppress("UNCHECKED_CAST")
        return when (ViewType.values()[viewType]) {
            ViewType.Body -> {
                val view = inflater
                    .inflate(listener.getItemLayout(), parent, false)
                listener.createViewHolder(view, viewType)
            }
            ViewType.Footer -> {
                val view = inflater
                    .inflate(R.layout.footer_item, parent, false)
                FooterViewHolder(view) as V
            }
        }

    }

    interface IBaseList<T, V> {
        fun createViewHolder(mView: View, viewType: Int = 0): V
        fun onClickItemListener(item: T)
        fun onBindViewHolder(item: T, viewHolder: V, position: Int)
        fun getItemLayout(): Int
        val itemNameRes: Int
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        when (ViewType.values()[getItemViewType(position)]) {
            ViewType.Body -> {
                val item = listLiveData.value?.get(position) ?: return
                listener.onBindViewHolder(item, holder, position)
                holder.itemView.setOnClickListener { listener.onClickItemListener(item) }
            }
            ViewType.Footer -> {
                val viewHolder = holder as? FooterViewHolder ?: return
                val meta = olderDirectionMeta.value
                when {
                    meta == null || meta.more == true -> {
                        // remain items
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.VISIBLE
                    }
                    meta.more == false -> {
                        // loaded all items
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                    }
                    else -> {
                        // empty
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.VISIBLE
                        val context = holder.itemView.context
                        val itemName = context.getString(listener.itemNameRes).toLowerCase()
                        viewHolder.noItemsMessageTextView.text =
                            context.getString(R.string.no_items_template, itemName)
                    }
                }
            }
        }

    }

    fun updateFooter() {
        notifyItemChanged(listLiveData.value?.size ?: 0)
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingIndicatorProgressBar: ProgressBar = itemView.loadingIndicatorProgressBar
        val noItemsMessageTextView: TextView = itemView.noItemsMessageTextView

    }
}
