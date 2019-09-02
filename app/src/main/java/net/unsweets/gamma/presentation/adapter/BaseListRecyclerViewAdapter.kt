package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.footer_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Unique
import java.util.*

class BaseListRecyclerViewAdapter<T : Unique, V : RecyclerView.ViewHolder>(
    private val options: BaseListRecyclerViewAdapterOptions<T, V>
) : RecyclerView.Adapter<V>() {
    data class BaseListRecyclerViewAdapterOptions<T, V>(
        val listLiveData: LiveData<ArrayList<T>>,
        val olderDirectionMeta: LiveData<PnutResponse.Meta>,
        var listener: IBaseList<T, V>,
        val reverse: Boolean = false,
        val mainItemId: String = ""
    )

    init {
        setHasStableIds(true)
    }
    // empty view; loading indicator;
    override fun getItemCount(): Int = bodyItemCount + 1

    private val bodyItemCount: Int
        get() = (options.listLiveData.value?.size ?: 0)

    var recyclerView: RecyclerView? = null

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemId(position: Int): Long {
        return options.listLiveData.value?.getOrNull(position)?.uniqueKey?.toLong() ?: 0L
    }

    private enum class ViewType { Body, Footer }

    override fun getItemViewType(position: Int): Int {
        return when (options.reverse) {
            true -> {
                val isFirstItem = position == 0
                return when {
                    isFirstItem -> ViewType.Footer
                    else -> ViewType.Body
                }.ordinal
            }
            false -> {
                val isLastItem = itemCount == position + 1
                when {
                    isLastItem -> ViewType.Footer
                    else -> ViewType.Body
                }.ordinal
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        val inflater = LayoutInflater.from(parent.context)
        @Suppress("UNCHECKED_CAST")
        return when (ViewType.values()[viewType]) {
            ViewType.Body -> {
                val view = inflater
                    .inflate(options.listener.getItemLayout(), parent, false)
                options.listener.createViewHolder(view, viewType)
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
        fun onBindViewHolder(item: T, viewHolder: V, position: Int, isMainItem: Boolean)
        fun getItemLayout(): Int
        val itemNameRes: Int
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        val offset = if (options.reverse) -1 else 0
        when (ViewType.values()[getItemViewType(position)]) {
            ViewType.Body -> {
                val itemPosition = position + offset
                val item = options.listLiveData.value?.get(itemPosition) ?: return
                holder.itemView.setOnClickListener { options.listener.onClickItemListener(item) }
                options.listener.onBindViewHolder(item, holder, itemPosition, options.mainItemId == item.uniqueKey)
            }
            ViewType.Footer -> {
                val viewHolder = holder as? FooterViewHolder ?: return
                val meta = options.olderDirectionMeta.value
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
                        val itemName = context.getString(options.listener.itemNameRes)
                            .toLowerCase(Locale.ENGLISH)
                        viewHolder.noItemsMessageTextView.text =
                            context.getString(R.string.no_items_template, itemName)
                    }
                }
            }
        }

    }

    fun updateFooter() {
        when (options.reverse) {
            true -> notifyItemChanged(0)
            false -> notifyItemChanged(options.listLiveData.value?.size ?: 0)
        }

    }

    fun updateItem(item: T) {
        val index = options.listLiveData.value?.indexOf(item) ?: -1
        if (index < 0) return
        options.listLiveData.value?.set(index, item)
        notifyItemRemoved(index)
    }

    fun removeItem(item: T) {
        val index = options.listLiveData.value?.indexOf(item) ?: -1
        if (index < 0) return
        options.listLiveData.value?.removeAt(index)
        notifyItemRemoved(index)
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingIndicatorProgressBar: ProgressBar = itemView.loadingIndicatorProgressBar
        val noItemsMessageTextView: TextView = itemView.noItemsMessageTextView

    }
}
