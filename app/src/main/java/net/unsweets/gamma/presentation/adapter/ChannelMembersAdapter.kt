package net.unsweets.gamma.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.unsweets.gamma.presentation.adapter.channelmember.ChannelMember
import net.unsweets.gamma.presentation.adapter.channelmember.ChannelMemberViewHolder
import net.unsweets.gamma.presentation.adapter.channelmember.ItemViewHolder
import net.unsweets.gamma.presentation.adapter.channelmember.TitleViewHolder
import net.unsweets.gamma.presentation.util.ManipulateList
import net.unsweets.gamma.util.Resource

class ChannelMembersAdapter(private val getItems: () -> Resource<List<ChannelMember>>) :
    RecyclerView.Adapter<ChannelMemberViewHolder>() {
    private val items
        get() = getItems().value.orEmpty()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChannelMemberViewHolder {
        return ViewType.values()[viewType].createViewHolder(this, parent)
    }

    override fun onBindViewHolder(holder: ChannelMemberViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    private fun getItem(position: Int) = items.getOrNull(position)

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.let {
            when (it) {
                is ChannelMember.Header -> ViewType.Header
                is ChannelMember.Item -> ViewType.User
            }.ordinal
        } ?: ViewType.Header.ordinal
    }


    fun manipulateList(manipulateList: ManipulateList) {
        val range = manipulateList.range
        val first = range.first
        val last = range.last - 1
        when (manipulateList) {
            is ManipulateList.Add -> notifyItemRangeInserted(first, last)
            is ManipulateList.Remove -> notifyItemRangeRemoved(first, last)
            is ManipulateList.Update -> notifyItemRangeChanged(first, last)
            ManipulateList.Error -> Unit
        }
    }

    private enum class ViewType(
        val createViewHolder:
            (RecyclerView.Adapter<ChannelMemberViewHolder>, ViewGroup) -> ChannelMemberViewHolder
    ) {
        Header({ adapter, parent ->
            TitleViewHolder.createViewHolder(
                adapter,
                parent
            )
        }),
        User({ adapter, parent ->
            ItemViewHolder.createViewHolder(
                adapter,
                parent
            )
        })
    }

    override fun getItemCount(): Int = items.size
}