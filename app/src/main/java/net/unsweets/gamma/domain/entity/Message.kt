package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.entities.Entities
import net.unsweets.gamma.domain.entity.entities.HaveEntities
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Message(
    @Json(name = "created_at") val createdAt: Date,
    val id: String,
    @Json(name = "is_deleted") val isDeleted: Boolean,
    @Json(name = "is_sticky") val isSticky: Boolean,
    val source: Client,
    @Json(name = "reply_to") val replyTo: String,
    @Json(name = "thread_id") val threadId: String,
    val user: User?,
    val content: MessageContent?,
    @Json(name = "pagination_id") override var paginationId: String? = null

) : Parcelable, UniquePageable {
    @IgnoredOnParcel
    override val uniqueKey by lazy { id }

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class MessageContent(
        override val entities: Entities?,
        override val html: String?,
        override val text: String?
    ) : HaveEntities, Parcelable
}




