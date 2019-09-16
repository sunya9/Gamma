package net.unsweets.gamma.domain.entity.raw.replacement

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.domain.entity.raw.PollNotice
import net.unsweets.gamma.domain.entity.raw.PostRaw
import net.unsweets.gamma.domain.entity.raw.Raw

@Parcelize
@JsonClass(generateAdapter = true)
data class PostPoll(override val value: PollNoticeValue) : PostRaw<Raw.RawValue>, Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class PollNoticeValue(
        @Json(name = "+io.pnut.core.poll") val value: ReplacementPollValue
    ) : Parcelable, Raw.RawValue {
        @IgnoredOnParcel
        val type = PollNotice.type

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class ReplacementPollValue(
            @Json(name = "poll_token") val pollToken: String,
            @Json(name = "poll_id") val pollId: String
        ) : Parcelable
    }

    @IgnoredOnParcel
    override val type: String = PollNotice.type

    companion object {
        fun createFromPoll(poll: Poll) = PostPoll(
            PollNoticeValue(
                PollNoticeValue.ReplacementPollValue(poll.pollToken, poll.id)
            )
        )
    }
}