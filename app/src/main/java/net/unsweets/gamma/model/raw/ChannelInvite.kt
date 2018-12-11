package net.unsweets.gamma.model.raw

import com.squareup.moshi.Json

data class ChannelInvite(override val value: ChannelInviteValue) : Raw.IRaw {
    override val type: String = "io.pnut.core.channel.invite"

    data class ChannelInviteValue(@Json(name = "channel_id") val channelId: String) : Raw.RawValue
}
