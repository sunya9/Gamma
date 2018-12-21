package net.unsweets.gamma.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.model.entities.Entities
import net.unsweets.gamma.model.entities.HaveEntities
import java.util.*

@Parcelize
data class Message(
    @Json(name = "created_at") val createdAt: Date,
    val id: String,
    @Json(name = "is_deleted") val isDeleted: Boolean,
    @Json(name = "is_sticky") val isSticky: Boolean,
    val source: Client,
    @Json(name = "reply_to") val replyTo: String,
    @Json(name = "thread_id") val threadId: String,
    val user: User?,
    val content: MessageContent?
) : Parcelable {
    @Parcelize
    data class MessageContent(
        override val entities: Entities?,
        override val html: String?,
        override val text: String?
    ) : HaveEntities, Parcelable
}


