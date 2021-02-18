package net.unsweets.gamma.presentation.adapter.channelmember

import androidx.annotation.StringRes
import net.unsweets.gamma.domain.entity.Channel

sealed class ChannelMember {
    data class Item(val limitedUser: Channel.LimitedUser) : ChannelMember()
    data class Header(@StringRes val titleRes: Int) : ChannelMember()
}