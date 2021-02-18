package net.unsweets.gamma.presentation.adapter.channelmember

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.unsweets.gamma.databinding.ManageChannelMembersHeaderBinding
import net.unsweets.gamma.databinding.ManageChannelMembersItemBinding

sealed class ChannelMemberViewHolder(
    protected val adapter: RecyclerView.Adapter<ChannelMemberViewHolder>,
    view: View
) : RecyclerView.ViewHolder(view) {
    protected val context: Context = view.context
    abstract fun bind(item: ChannelMember)
    abstract class Item(adapter: RecyclerView.Adapter<ChannelMemberViewHolder>, view: View) :
        ChannelMemberViewHolder(adapter, view) {
        protected val binding = ManageChannelMembersItemBinding.bind(view)
    }

    abstract class Header(adapter: RecyclerView.Adapter<ChannelMemberViewHolder>, view: View) :
        ChannelMemberViewHolder(adapter, view) {
        protected val binding = ManageChannelMembersHeaderBinding.bind(view)
        abstract fun bind(title: String)
    }

    fun interface CreateViewHolder {
        fun createViewHolder(
            adapter: RecyclerView.Adapter<ChannelMemberViewHolder>,
            parent: ViewGroup
        ): ChannelMemberViewHolder
    }
}


