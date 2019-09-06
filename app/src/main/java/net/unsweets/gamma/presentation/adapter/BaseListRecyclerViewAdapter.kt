package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.segment_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Pageable
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Unique
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.util.LogUtil
import java.util.*

class BaseListRecyclerViewAdapter<T, V : RecyclerView.ViewHolder>(
    private val options: BaseListRecyclerViewAdapterOptions<T, V>
) : RecyclerView.Adapter<V>() where T : Unique, T : Pageable {
    data class BaseListRecyclerViewAdapterOptions<TT, VV>(
        val itemList: ArrayList<PageableItemWrapper<TT>>,
        var listener: IBaseList<TT, VV>,
        val reverse: Boolean = false,
        val mainItemId: String = ""
    ) where TT : Unique, TT : Pageable

    fun init() {
        setupHeaderSegment()
        setupFooterSegment()
    }

    private fun setupFooterSegment() {
        // footer
//        val index = if(options.reverse) 0 else options.itemList.size
        val index = options.itemList.size
        val lastItem = options.itemList.lastOrNull()
        val minId = (lastItem as? PageableItemWrapper.Item)?.item?.paginationId
        options.itemList.add(
            index,
            PageableItemWrapper.Pager(
                minId = minId,
                maxId = null,
                more = true,
                state = PageableItemWrapper.Pager.State.None
            )
        )
    }

    private fun setupHeaderSegment() {
        // only has cache
        if (options.itemList.isEmpty()) return
        val firstItem = options.itemList.firstOrNull()
        // return if cannot get pagination id of first item
        val maxId = (firstItem as? PageableItemWrapper.Item)?.item?.paginationId ?: return
        options.itemList.add(
            0,
            PageableItemWrapper.Pager(
                more = true,
                maxId = maxId,
                minId = null,
                state = PageableItemWrapper.Pager.State.None
            )
        )
    }

    // empty view; loading indicator;
    override fun getItemCount(): Int = bodyItemCount

    private val bodyItemCount: Int
        get() = options.itemList.size

    var recyclerView: RecyclerView? = null

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    private enum class ViewType { Body, Segment }

    override fun getItemViewType(position: Int): Int {
        return when (options.reverse) {
            true -> {
//                val isFirstItem = position == 0
                return when {
//                    isFirstItem -> ViewType.Segment
                    else -> when (options.itemList[position]) {
                        is PageableItemWrapper.Pager -> ViewType.Segment
                        else -> ViewType.Body
                    }
                }.ordinal
            }
            false -> {
//                val isLastItem = itemCount == position + 1
                when {
//                    isLastItem -> ViewType.Segment
                    else -> when (options.itemList[position]) {
                        is PageableItemWrapper.Pager -> ViewType.Segment
                        else -> ViewType.Body
                    }
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
            ViewType.Segment -> {
                val view = inflater
                    .inflate(R.layout.segment_item, parent, false)
                val viewHolder = SegmentViewHolder(view)
                viewHolder.retryButton.setOnClickListener { options.listener.retryCallback() }
                viewHolder as V
            }
        }
    }

    interface IBaseList<T, V> where T : Unique, T : Pageable {
        fun createViewHolder(mView: View, viewType: Int = 0): V
        fun onClickItemListener(viewHolder: V, item: T, itemWrapper: PageableItemWrapper<T>)
        fun onBindViewHolder(item: T, viewHolder: V, position: Int, isMainItem: Boolean)
        fun getItemLayout(): Int
        val itemNameRes: Int
        fun retryCallback()
    }

    fun updateSegment(info: PageableItemWrapper.Pager<T>, receivedMeta: PnutResponse.Meta) {
        val position = options.itemList.indexOfFirst {
            LogUtil.e("${it.getBeforeId()}, ${info.minId}, ${it.getSinceId()}, ${info.maxId}")
            it is PageableItemWrapper.Pager && (it.getBeforeId() == info.minId || it.getSinceId() == info.maxId)
        }
        LogUtil.e("position $position")
        LogUtil.e("updateSegment $info ${options.itemList.last()}")
//        LogUtil.e("position $position itemSize: ${options.itemList.size}")
        if (position < 0) return
        val updatedInfo = info.copy(
            maxId = receivedMeta.max_id,
            minId = receivedMeta.min_id,
            more = receivedMeta.more ?: false,
            state = PageableItemWrapper.Pager.State.None
        )
        when {
            isFooterSegment(position) -> {
                LogUtil.e("isFooterSegment")
                options.itemList[position] = updatedInfo
                notifyItemChanged(position)
            }
            updatedInfo.more -> {
                LogUtil.e("more is true")
                options.itemList[position] = updatedInfo
                notifyItemChanged(position)
            }
            else -> {
                notifyItemRemoved(position)
            }
        }

    }

    private fun isFooterSegment(position: Int): Boolean {
        return bodyItemCount - 1 == position
    }

    override fun onBindViewHolder(holder: V, position: Int) {
//        val offset = if (options.reverse) -1 else 0
        //        LogUtil.e("options.itemList[itemPosition] ${options.itemList[itemPosition]}")
        when (ViewType.values()[getItemViewType(position)]) {
            ViewType.Body -> {
                val itemWrapper =
                    options.itemList[position] as? PageableItemWrapper.Item ?: return
                val item = itemWrapper.item
                holder.itemView.setOnClickListener {
                    options.listener.onClickItemListener(
                        holder,
                        item,
                        itemWrapper
                    )
                }
                options.listener.onBindViewHolder(
                    item,
                    holder,
                    position,
                    options.mainItemId == item.uniqueKey
                )
            }
            ViewType.Segment -> {
                val pager =
                    options.itemList[position] as? PageableItemWrapper.Pager ?: return
                val viewHolder = holder as? SegmentViewHolder ?: return
                when {
                    pager.state is PageableItemWrapper.Pager.State.Error -> {
                        viewHolder.retryButton.visibility = View.VISIBLE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.endOfListImageView.visibility = View.GONE
                    }
                    pager.state is PageableItemWrapper.Pager.State.Loading -> {
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.VISIBLE
                        viewHolder.endOfListImageView.visibility = View.GONE
                    }
                    pager.more -> {
                        // remain items
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.VISIBLE
                        viewHolder.endOfListImageView.visibility = View.GONE
                    }
                    !pager.more && position == 0 -> {
                        // empty
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.VISIBLE
                        viewHolder.endOfListImageView.visibility = View.GONE
                        val context = holder.itemView.context
                        val itemName = context.getString(options.listener.itemNameRes)
                            .toLowerCase(Locale.ENGLISH)
                        viewHolder.noItemsMessageTextView.text =
                            context.getString(R.string.no_items_template, itemName)
                    }
                    else -> {
                        // loaded all items
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.endOfListImageView.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    fun updateItem(item: PageableItemWrapper<T>) {
        LogUtil.e(item.toString())
        val index = options.itemList.indexOfFirst {
            LogUtil.e("${it} ${it.uniqueKey}")
            it.uniqueKey == item.uniqueKey
        }
        LogUtil.e("index $index")
        if (index < 0) return
        options.itemList[index] = item
        notifyItemChanged(index)
    }

    fun removeItem(item: PageableItemWrapper<T>) {
        val index = options.itemList.indexOf(item)
        if (index < 0) return
        options.itemList.removeAt(index)
        notifyItemRemoved(index)
    }

    fun showRetryMessage() {
        notifyItemChanged(bodyItemCount)
    }

    fun addItem() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class SegmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingIndicatorProgressBar: ProgressBar = itemView.loadingIndicatorProgressBar
        val noItemsMessageTextView: TextView = itemView.noItemsMessageTextView
        val retryButton: MaterialButton = itemView.retryButton
        val endOfListImageView: ImageView = itemView.endOfListImageView
    }
}
