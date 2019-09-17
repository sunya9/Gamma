package net.unsweets.gamma.domain.entity.raw

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.domain.entity.PollLikeValue
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class PollNotice(override val value: PollNoticeValue) : Raw<PollNotice.PollNoticeValue>,
    Parcelable {
    @IgnoredOnParcel
    override val type: String = PollNotice.type

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class PollNoticeValue(
        override val prompt: String,
        @Json(name = "poll_token") override val pollToken: String,
        @Json(name = "closed_at") override val closedAt: Date,
        @Json(name = "poll_id") val pollId: String,
        override val options: List<Poll.PollOption>
    ) : Raw.RawValue, Parcelable, PollLikeValue {
        @IgnoredOnParcel
        override val id: String = pollId
    }

    companion object {
        const val type = "io.pnut.core.poll-notice"
        fun findPollNotice(raw: List<Raw<*>>?): PollNotice? =
            raw?.find { it is PollNotice } as? PollNotice
    }
}