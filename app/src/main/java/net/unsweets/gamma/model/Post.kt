package net.unsweets.gamma.model

import com.squareup.moshi.Json
import net.unsweets.gamma.model.entities.Entities
import net.unsweets.gamma.model.entities.HaveEntities
import java.util.*

data class Post(
    @Json(name = "created_at") val createdAt: Date,
    val id: String,
    @Json(name = "is_deleted") val isDeleted: Boolean?,
    @Json(name = "is_nsfw") val isNsfw: Boolean?,
    @Json(name = "is_revised") val isRevised: Boolean?,
    val revision: String?,
    val source: Client,
    val user: User,
    @Json(name = "thread_id") val threadId: String,
    @Json(name = "reply_to") val replyTo: String?,
    @Json(name = "repost_of") val repostOf: Post?,
    val counts: PostCount,
    val content: PostContent?,
    @Json(name = "you_bookmarked") val youBookmarked: Boolean?,
    @Json(name = "you_reposted") val youReposted: Boolean?
) {
    data class PostContent(
        override val text: String?,
        override val html: String?,
        override val entities: Entities?,
        @Json(name = "link_not_parsed") val linksNotParsed: Boolean?
    ) : HaveEntities

    data class PostCount(
        val bookmarks: Int,
        val replies: Int,
        val reposts: Int,
        val threads: Int
    )
}
