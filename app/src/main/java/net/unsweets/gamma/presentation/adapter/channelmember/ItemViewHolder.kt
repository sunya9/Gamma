package net.unsweets.gamma.presentation.adapter.channelmember

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.GlideApp

class ItemViewHolder(adapter: RecyclerView.Adapter<ChannelMemberViewHolder>, view: View) :
    ChannelMemberViewHolder.Item(adapter, view) {
    val request = GlideApp.with(context)

    override fun bind(channelMember: ChannelMember) {
        if (channelMember !is ChannelMember.Item) return
        val limitedUser = channelMember.limitedUser
        request.load(limitedUser.avatarImage).into(binding.channelMemberAvatarImageView)
        binding.channelMemberUsernameTextView.text =
            context.getString(R.string.username_template, limitedUser.username)
        binding.channelMemberNameTextView.text = limitedUser.name
    }

    companion object : CreateViewHolder {
        override fun createViewHolder(
            adapter: RecyclerView.Adapter<ChannelMemberViewHolder>,
            parent: ViewGroup
        ): ChannelMemberViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_channel_members_item, parent, false)
            return ItemViewHolder(adapter, view)
        }
    }
}