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
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.UniquePageable
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.util.LogUtil
import java.util.*

class BaseListRecyclerViewAdapter<T : UniquePageable, V : RecyclerView.ViewHolder>(
    private val options: BaseListRecyclerViewAdapterOptions<T, V>
) : RecyclerView.Adapter<V>() {
    data class BaseListRecyclerViewAdapterOptions<TT : UniquePageable, VV>(
        val itemList: ArrayList<PageableItemWrapper<TT>>,
        var listener: IBaseList<TT, VV>,
        val reverse: Boolean = false,
        val mainItemId: String = ""
    )

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
                viewHolder as V
            }
        }
    }

    interface IBaseList<T : UniquePageable, V> {
        fun createViewHolder(mView: View, viewType: Int = 0): V
        fun onClickItemListener(viewHolder: V, item: T, itemWrapper: PageableItemWrapper<T>)
        fun onBindViewHolder(item: T, viewHolder: V, position: Int, isMainItem: Boolean)
        fun getItemLayout(): Int
        val itemNameRes: Int
        fun onClickSegmentListener(
            viewHolder: SegmentViewHolder,
            itemWrapper: PageableItemWrapper.Pager<T>
        )
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
                    pager.state == PageableItemWrapper.Pager.State.Error -> {
                        viewHolder.retryButton.visibility = View.VISIBLE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.endOfListImageView.visibility = View.GONE
                        viewHolder.segmentImageView.visibility = View.GONE
                        setClickableSegment(viewHolder, pager) {
                            viewHolder.retryButton.visibility = View.GONE
                            viewHolder.loadingIndicatorProgressBar.visibility = View.VISIBLE
//                        options.itemList[position] = pager.copy(state = PageableItemWrapper.Pager.State.Loading)
                            notifyItemChanged(
                                position,
                                pager.copy(state = PageableItemWrapper.Pager.State.Loading)
                            )
                        }
                    }
                    pager.state == PageableItemWrapper.Pager.State.Loading -> {
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.VISIBLE
                        viewHolder.endOfListImageView.visibility = View.GONE
                        viewHolder.segmentImageView.visibility = View.GONE
                        setClickableSegment(viewHolder, pager)
                    }
                    pager.more -> {
                        // remain items
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.endOfListImageView.visibility = View.GONE
                        viewHolder.segmentImageView.visibility = View.VISIBLE
                        setClickableSegment(viewHolder, pager) {
                            viewHolder.segmentImageView.visibility = View.GONE
                            viewHolder.loadingIndicatorProgressBar.visibility = View.VISIBLE
//                        options.itemList[position] = pager.copy(state = PageableItemWrapper.Pager.State.Loading)
                            notifyItemChanged(
                                position,
                                pager.copy(state = PageableItemWrapper.Pager.State.Loading)
                            )
                        }

                    }
                    !pager.more && position == 0 -> {
                        // empty
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.VISIBLE
                        viewHolder.endOfListImageView.visibility = View.GONE
                        viewHolder.segmentImageView.visibility = View.GONE
                        val context = holder.itemView.context
                        val itemName = context.getString(options.listener.itemNameRes)
                            .toLowerCase(Locale.ENGLISH)
                        viewHolder.noItemsMessageTextView.text =
                            context.getString(R.string.no_items_template, itemName)
                        disableSegment(viewHolder)

                    }
                    else -> {
                        // loaded all items
                        viewHolder.retryButton.visibility = View.GONE
                        viewHolder.noItemsMessageTextView.visibility = View.GONE
                        viewHolder.loadingIndicatorProgressBar.visibility = View.GONE
                        viewHolder.endOfListImageView.visibility = View.VISIBLE
                        viewHolder.segmentImageView.visibility = View.GONE
                        disableSegment(viewHolder)
                    }
                }
            }
        }
    }

    private fun setClickableSegment(
        viewHolder: SegmentViewHolder,
        pager: PageableItemWrapper.Pager<T>,
        onClick: () -> Unit = {}
    ) {
        viewHolder.itemView.isEnabled = true
        viewHolder.itemView.setOnClickListener {
            options.listener.onClickSegmentListener(viewHolder, pager)
            it.isEnabled = false
            onClick()
        }
    }

    private fun disableSegment(viewHolder: SegmentViewHolder) {
        viewHolder.itemView.setOnClickListener(null)

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

    fun removeSegmentIfNeed(requestPager: PageableItemWrapper<T>?): Int {
        if (requestPager == null) return 0
        val willRemoveSegmentIndex = options.itemList.indexOf(requestPager)
        if (willRemoveSegmentIndex < 0) return 0
        LogUtil.e("willRemoveSegmentIndex $willRemoveSegmentIndex")
        options.itemList.removeAt(willRemoveSegmentIndex)
        notifyItemRemoved(willRemoveSegmentIndex)
        return willRemoveSegmentIndex
    }

    private fun addSegmentIfNeed(
        index: Int,
        requestPager: PageableItemWrapper<T>?,
        response: PnutResponse<List<T>>
    ) {
        LogUtil.e("addSegmentIfNeed $index, ${options.itemList.size}")
        if (response.meta.more == true) {
            val baseSegment = PageableItemWrapper.Pager.createFromMeta(response.meta, requestPager)
            val segment = baseSegment
                .takeIf { index == options.itemList.size - 1 }
                ?.copy(maxId = null) ?: baseSegment
            LogUtil.e("add more segment $segment")
            options.itemList.add(index, segment)
            notifyItemInserted(index)
        } else if (response.meta.more == false && index == options.itemList.size) {
            val footerIndex = index
            LogUtil.e("add footer segment")
            // Add the footer segment if insert position is end of list and more is false
            options.itemList.add(
                footerIndex,
                PageableItemWrapper.Pager.createFromMeta(response.meta, requestPager)
            )
            notifyItemInserted(footerIndex)
        } else if (response.meta.more == false && 0 == index && 0 == options.itemList.size) {
            // no items footer segment
            options.itemList.add(
                PageableItemWrapper.Pager.createFromMeta(
                    response.meta,
                    requestPager
                )
            )
            notifyItemInserted(0)
        }
    }

    fun insertItems(
        requestPager: PageableItemWrapper<T>?,
        response: PnutResponse<List<T>>,
        insertIndex: Int
    ) {
        val items = response.data
        val isFirstTime = options.itemList.isEmpty()
        val pageableItemWrapperItems = items.map { PageableItemWrapper.Item(it) }
        LogUtil.e("requestPager $requestPager ")
        LogUtil.e("itemsS size ${pageableItemWrapperItems.size}")
        LogUtil.e("Insert range $insertIndex ${insertIndex + items.size}")

        options.itemList.addAll(insertIndex, pageableItemWrapperItems)
        // scroll to top when insert in first time
        if (isFirstTime)
            notifyItemRangeChanged(insertIndex, items.size)
        else
            notifyItemRangeInserted(insertIndex, items.size)

        addSegmentIfNeed(insertIndex + items.size, requestPager, response)
    }

    class SegmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loadingIndicatorProgressBar: ProgressBar = itemView.loadingIndicatorProgressBar
        val noItemsMessageTextView: TextView = itemView.noItemsMessageTextView
        val retryButton: MaterialButton = itemView.retryButton
        val endOfListImageView: ImageView = itemView.endOfListImageView
        val segmentImageView: ImageView = itemView.segmentImageView
    }
}
