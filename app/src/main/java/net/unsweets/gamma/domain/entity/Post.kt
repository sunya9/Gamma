package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.entities.Entities
import net.unsweets.gamma.domain.entity.entities.HaveEntities
import net.unsweets.gamma.domain.entity.raw.Raw
import net.unsweets.gamma.domain.entity.raw.Spoiler
import java.util.*

@Parcelize
data class Post(
    @Json(name = "created_at") var createdAt: Date,
    var id: String,
    @Json(name = "is_deleted") var isDeleted: Boolean?,
    @Json(name = "is_nsfw") var isNsfw: Boolean?,
    @Json(name = "is_revised") var isRevised: Boolean?,
    var revision: String?,
    var source: Client?,
    var user: User?,
    @Json(name = "thread_id") var threadId: String?,
    @Json(name = "reply_to") var replyTo: String?,
    @Json(name = "repost_of") var repostOf: Post?,
    var counts: PostCount?,
    var content: PostContent?,
    @Json(name = "you_bookmarked") var youBookmarked: Boolean?,
    @Json(name = "you_reposted") var youReposted: Boolean?,
    @Json(name = "pagination_id") override var paginationId: String?,
    @Json(name = "raw") var raw: List<Raw<*>>?,
    @Json(name = "bookmarked_by") val bookmarkedBy: List<User>?,
    @Json(name = "reposted_by") val repostedBy: List<User>?
) : Parcelable, Pageable, Unique {
    @IgnoredOnParcel
    override val uniqueKey: String by lazy { id }

    @Parcelize
    data class PostContent(
        override var text: String?,
        override var html: String?,
        override var entities: Entities?,
        @Json(name = "link_not_parsed") var linksNotParsed: Boolean?
    ) : HaveEntities, Parcelable

    @Parcelize
    data class PostCount(
        var bookmarks: Int,
        var replies: Int,
        var reposts: Int,
        var threads: Int
    ) : Parcelable

    @IgnoredOnParcel
    val mainPost: Post = repostOf ?: this
    @IgnoredOnParcel
    var nsfwMask = isNsfw ?: false
    @IgnoredOnParcel
    var spoilerMask = false
    @IgnoredOnParcel
    val showContents: Boolean
        get() = !nsfwMask && !spoilerMask
    @IgnoredOnParcel
    val spoiler = raw?.let { Spoiler.getSpoilerRaw(it) }

    init {
        spoilerMask = spoiler?.let {
            val spoilerDate = it.value.expiredAt ?: return@let true
            spoilerDate.time > Calendar.getInstance().time.time
        } ?: false
    }

    @IgnoredOnParcel
    val reactionUsers: List<User> = mutableListOf<User>().let {
        if (bookmarkedBy != null) it.addAll(bookmarkedBy)
        if (repostedBy != null) it.addAll(repostedBy)
        it.distinctBy { user -> user.id }
    }
}
