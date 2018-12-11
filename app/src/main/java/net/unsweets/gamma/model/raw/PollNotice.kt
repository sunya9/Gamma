package net.unsweets.gamma.model.raw

import com.squareup.moshi.Json
import net.unsweets.gamma.model.Poll

data class PollNotice(override val value: PollValue) : Raw.IRaw {
    override val type: String = "io.pnut.core.poll-notice"

    data class PollValue(
        val prompt: String,
        @Json(name = "poll_token") val pollToken: String,
        @Json(name = "closed_at") val closedAt: String,
        @Json(name = "poll_id") val pollId: String,
        val options: List<Poll.PollOption>
    ) : Raw.RawValue
}