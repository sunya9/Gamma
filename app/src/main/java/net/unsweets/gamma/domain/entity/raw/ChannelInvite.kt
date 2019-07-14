package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChannelInvite(override val value: ChannelInviteValue) : Raw<ChannelInvite.ChannelInviteValue>,
    PostRaw<ChannelInvite.ChannelInviteValue>, Parcelable {
    @IgnoredOnParcel
    override val type: String = "io.pnut.core.channel.invite"

    @Parcelize
    data class ChannelInviteValue(@Json(name = "channel_id") val channelId: String) : Raw.RawValue, Parcelable
}
