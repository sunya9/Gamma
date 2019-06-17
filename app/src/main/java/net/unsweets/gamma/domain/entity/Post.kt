package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.entities.Entities
import net.unsweets.gamma.domain.entity.entities.HaveEntities
import java.util.*

@Parcelize
@Entity
data class Post(
    @Json(name = "created_at") var createdAt: Date,
    @PrimaryKey var id: String,
    @Json(name = "is_deleted") var isDeleted: Boolean?,
    @Json(name = "is_nsfw") var isNsfw: Boolean?,
    @Json(name = "is_revised") var isRevised: Boolean?,
    var revision: String?,
    @Ignore var source: Client?,
    @Ignore var user: User?,
    @Json(name = "thread_id") var threadId: String?,
    @Json(name = "reply_to") var replyTo: String?,
    @Ignore @Json(name = "repost_of") var repostOf: Post?,
    @Ignore var counts: PostCount?,
    @Ignore var content: PostContent?,
    @Json(name = "you_bookmarked") var youBookmarked: Boolean?,
    @Json(name = "you_reposted") var youReposted: Boolean?,
    @Json(name = "pagination_id") override var paginationId: String?
): Parcelable, Pageable {
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

    fun getMainPost(): Post = repostOf ?: this

    constructor() : this(
        Date(),
        "-1",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        ""
    )
}
