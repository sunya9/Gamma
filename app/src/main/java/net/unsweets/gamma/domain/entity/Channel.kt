package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
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
    @Json(name = "has_unread") val hasUnread: Boolean,
    @Json(name = "pagination_id") override var paginationId: String? = null
) : Parcelable, UniquePageable {
    @IgnoredOnParcel
    override val uniqueKey: String by lazy { id }
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ChannelCount(
        val messages: Int,
        val subscribers: Int
    ) : Parcelable

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Acl(
        val full: Full,
        val write: Write,
        val read: Read
    ) : Parcelable {
        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Full(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<String>
        ) : IAuthority, Parcelable

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Write(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<String>,
            @Json(name = "any_user") val anyUser: Boolean
        ) : IAuthority, Parcelable

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Read(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<String>,
            @Json(name = "any_user") val anyUser: Boolean,
            val public: Boolean
        ) : IAuthority, Parcelable

        private interface IAuthority {
            val immutable: Boolean
            val you: Boolean
            @Json(name = "user_ids")
            val userIds: List<String>
        }
    }


}
