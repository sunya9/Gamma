package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.entities.Entities
import net.unsweets.gamma.domain.entity.entities.HaveEntities
import net.unsweets.gamma.domain.entity.image.Avatar
import net.unsweets.gamma.domain.entity.image.Cover
import java.util.*

@Parcelize
data class User(
    val badge: Badge?,
    val content: UserContent,
    val counts: UserCount,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "follows_you") val followsYou: Boolean,
    val id: String,
    val locale: String,
    val name: String?,
    val timezone: String,
    val type: AccountType,
    val username: String,
    @Json(name = "you_blocked") val youBlocked: Boolean,
    @Json(name = "you_can_follow") val youCanFollow: Boolean,
    @Json(name = "you_follow") val youFollow: Boolean,
    @Json(name = "you_muted") val youMuted: Boolean,
    val verified: VerifiedDomain?,
    @Json(name = "pagination_id") override val paginationId: String? = null
) : Parcelable, Pageable, Unique {
    @IgnoredOnParcel
    override val uniqueKey: String by lazy { id }

    @Parcelize
    data class UserContent(
        @Json(name = "avatar_image") val avatarImage: Avatar,
        @Json(name = "cover_image") val coverImage: Cover,
        override val entities: Entities?,
        override val html: String?,
        val markdownText: String?,
        override val text: String?
    ) : HaveEntities, Parcelable

    @Parcelize
    data class UserCount(
        val bookmarks: Int,
        val clients: Int,
        val followers: Int,
        val following: Int,
        val posts: Int,
        val users: Int
    ) : Parcelable

    @Parcelize
    data class Badge(
        val id: String,
        val name: String
    ) : Parcelable

    @Parcelize
    data class VerifiedDomain(
        val domain: String,
        val link: String
    ) : Parcelable

    enum class AccountType {
        @Json(name = "human")
        HUMAN,
        @Json(name = "feed")
        FEED,
        @Json(name = "bot")
        BOT
    }

    @IgnoredOnParcel
    val me = followsYou && youFollow && !youCanFollow

    enum class AvatarSize(val size: Int) { Mini(24), Small(48), Normal(64), Large(96) }

    fun getAvatarUrl(avatarSize: AvatarSize = AvatarSize.Normal) =
        "https://api.pnut.io/v0/users/$id/avatar?h=${avatarSize.size}"
}
