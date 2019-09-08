package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Poll(
    @Json(name ="closed_at") val closedAt: Date,
    @Json(name = "created_at") val createdAt: Date,
    val id: String,
    @Json(name = "is_anonymous") val isAnonymous: Boolean,
    @Json(name = "is_public") val isPublic: Boolean,
    val options: List<PollOption>,
    @Json(name = "poll_token") val pollToken: String,
    val prompt: String,
    val source: Client,
    val type: String,
    val user: User?
) : Parcelable {
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class PollOption(
        val text: String,
        val position: Int,
        @Json(name = "is_your_response") val isYourResponse: Boolean?,
        val respondents :Int?,
        @Json(name = "respondent_ids") val respondentIds: List<String>?
    ) : Parcelable
}

