package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Poll(
    @Json(name = "closed_at") override val closedAt: Date,
    @Json(name = "created_at") val createdAt: Date,
    override val id: String,
    @Json(name = "is_anonymous") val isAnonymous: Boolean,
    @Json(name = "is_public") val isPublic: Boolean,
    @Json(name = "max_options") val maxOptions: Int,
    override val options: List<PollOption>,
    @Json(name = "poll_token") override val pollToken: String,
    override val prompt: String,
    val source: Client,
    val type: String,
    val user: User?
) : Parcelable, PollLikeValue {
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class PollOption(
        val text: String,
        val position: Int,
        @Json(name = "is_your_response") val isYourResponse: Boolean? = null,
        val respondents: Int? = null,
        @Json(name = "respondent_ids") val respondentIds: List<String>? = null
    ) : Parcelable {
        fun getPercent(total: Int?) =
            if (total == null || total == 0 || respondents == null) 0 else (respondents.toFloat() / total.toFloat() * 100).toInt()
    }

    val total
        get() = options.fold(0) { acc, operation ->
            acc + (operation.respondents ?: 0)
        }
}

