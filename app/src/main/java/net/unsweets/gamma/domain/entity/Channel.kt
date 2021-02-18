package net.unsweets.gamma.domain.entity

import android.content.Context
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.raw.ChatSettings
import net.unsweets.gamma.domain.entity.raw.Raw

@Parcelize
@JsonClass(generateAdapter = true)
data class Channel(
    val id: String,
    @Json(name = "is_active") val isActive: Boolean? = null,
    val type: String,
    val owner: User? = null,
    @Json(name = "recent_message_id") val recentMessageId: String? = null,
    @Json(name = "recent_message") val recentMessage: Message? = null,
    val acl: Acl,
    val counts: ChannelCount? = null,
    @Json(name = "you_subscribed") val youSubscribed: Boolean,
    @Json(name = "you_muted") val youMuted: Boolean,
    @Json(name = "has_unread") val hasUnread: Boolean,
    @Json(name = "pagination_id") override var paginationId: String? = null,
    var raw: List<Raw<*>>? = null,
) : Parcelable, UniquePageable {
    @IgnoredOnParcel
    override val uniqueKey: String by lazy { id }

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class ChannelCount(
        val messages: Int,
        val subscribers: Int? = null
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
            @Json(name = "user_ids") override val userIds: List<LimitedUser>
        ) : IAuthority, Parcelable

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Write(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<LimitedUser>,
            @Json(name = "any_user") val anyUser: Boolean
        ) : IAuthority, Parcelable

        @Parcelize
        @JsonClass(generateAdapter = true)
        data class Read(
            override val immutable: Boolean,
            override val you: Boolean,
            @Json(name = "user_ids") override val userIds: List<LimitedUser>,
            @Json(name = "any_user") val anyUser: Boolean,
            val public: Boolean
        ) : IAuthority, Parcelable

        private interface IAuthority {
            val immutable: Boolean
            val you: Boolean

            @Json(name = "user_ids")
            val userIds: List<LimitedUser>
        }
    }

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class LimitedUser(
        val username: String,
        val id: String,
        val name: String?,
        @Json(name = "avatar_image") val avatarImage: String
    ) : Parcelable

    @IgnoredOnParcel
    val isPrivate = type == privateChannelType

    @IgnoredOnParcel
    val isPublic = type == publicChannelType

    @IgnoredOnParcel
    val chatSettings = raw?.let { ChatSettings.getChatSettingsRaw(it) }

    val hasFullPermission = acl.full.you
    fun isOwner(id: String): Boolean = owner?.id === id

    fun title(context: Context): String {
        return when {
            isPublic -> chatSettings?.value?.name.orEmpty()
            isPrivate -> context.getString(
                R.string.private_channel_title_template,
                id
            )
            else -> ""
        }
    }

    companion object {
        const val privateChannelType = "io.pnut.core.pm"
        const val publicChannelType = "io.pnut.core.chat"
    }
}
