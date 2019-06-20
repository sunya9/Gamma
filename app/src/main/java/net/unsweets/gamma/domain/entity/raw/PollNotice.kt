package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.Poll

@Parcelize
@JsonClass(generateAdapter = true)
data class PollNotice(override val value: PollValue) : Raw<PollNotice.PollValue>(), Parcelable {
    override val type: String = "io.pnut.core.poll-notice"

    @Parcelize
    data class PollValue(
        val prompt: String,
        @Json(name = "poll_token") val pollToken: String,
        @Json(name = "closed_at") val closedAt: String,
        @Json(name = "poll_id") val pollId: String,
        val options: List<Poll.PollOption>
    ) : Raw.RawValue, Parcelable
}