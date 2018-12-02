package net.unsweets.gamma.model

import com.squareup.moshi.Json

data class Channel(
    val id: String,
    @Json(name = "is_active") val isActive: Boolean,
    val type: String,
    val owner: User,
    @Json(name = "recent_message_id") val recentMessageId: String,
    @Json(name = "recent_message") val recentMessage: Message,
    val acl: Acl,
    val counts: ChannelCount,
    @Json(name = "you_subscribed") val youSubscribed: Boolean,
    @Json(name = "you_muted") val youMuted: Boolean,
    @Json(name = "has_unread") val hasUnread: Boolean
) {
    data class ChannelCount(
        val messages: Int,
        val subscribers: Int
    )

    data class Acl(
        val full: Full,
        val write: Write,
        val read: Read
    ) {
        data class Full(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<String>): IAuthority
        data class Write(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<String>,
            @Json(name ="any_user") val anyUser: Boolean
        ) : IAuthority

        data class Read(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<String>,
            @Json(name = "any_user") val anyUser: Boolean,
            val public: Boolean
        ) : IAuthority

        private interface IAuthority {
            val immutable: Boolean
            val you: Boolean
            @Json(name = "user_ids") val userIds: List<String>
        }
    }





}
