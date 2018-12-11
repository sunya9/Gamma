package net.unsweets.gamma.model

import com.squareup.moshi.Json
import net.unsweets.gamma.model.entities.Entities
import net.unsweets.gamma.model.entities.HaveEntities
import java.util.*

data class Message private constructor(
    @Json(name = "created_at") val createdAt: Date,
    val id: String,
    @Json(name = "is_deleted") val isDeleted: Boolean,
    @Json(name = "is_sticky") val isSticky: Boolean,
    val source: Client,
    @Json(name = "reply_to") val replyTo: String,
    @Json(name = "thread_id") val threadId: String,
    val user: User?,
    val content: MessageContent?
) {
    data class MessageContent(
        override val entities: Entities?,
        override val html: String?,
        override val text: String?
    ) : HaveEntities
}


