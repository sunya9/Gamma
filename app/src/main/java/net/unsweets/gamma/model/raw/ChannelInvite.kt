package net.unsweets.gamma.model.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChannelInvite(override val value: ChannelInviteValue) : Raw.IRaw, Parcelable {
    override val type: String = "io.pnut.core.channel.invite"

    @Parcelize
    data class ChannelInviteValue(@Json(name = "channel_id") val channelId: String) : Raw.RawValue, Parcelable
}
