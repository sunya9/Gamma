package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ChannelInvite(override val value: ChannelInviteValue) : Raw<ChannelInvite.ChannelInviteValue>,
    PostRaw<ChannelInvite.ChannelInviteValue>, Parcelable {
    @IgnoredOnParcel
    override val type: String = ChannelInvite.type

    @Parcelize
    data class ChannelInviteValue(@Json(name = "channel_id") val channelId: String) : Raw.RawValue, Parcelable

    companion object {
        const val type = "io.pnut.core.channel.invite"
    }
}
