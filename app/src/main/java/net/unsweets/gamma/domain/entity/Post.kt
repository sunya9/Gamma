package net.unsweets.gamma.domain.entity

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.domain.entity.entities.Entities
import net.unsweets.gamma.domain.entity.entities.HaveEntities
import net.unsweets.gamma.domain.entity.raw.PollNotice
import net.unsweets.gamma.domain.entity.raw.Raw
import net.unsweets.gamma.domain.entity.raw.Spoiler
import net.unsweets.gamma.presentation.adapter.PollOptionsAdapter
import net.unsweets.gamma.util.LogUtil
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Post(
    @Json(name = "created_at") var createdAt: Date,
    var id: String,
    @Json(name = "is_deleted") var isDeleted: Boolean? = null,
    @Json(name = "is_nsfw") var isNsfw: Boolean? = null,
    @Json(name = "is_revised") var isRevised: Boolean? = null,
    var revision: String? = null,
    var source: Client? = null,
    var user: User? = null,
    @Json(name = "thread_id") var threadId: String? = null,
    @Json(name = "reply_to") var replyTo: String? = null,
    @Json(name = "repost_of") var repostOf: Post? = null,
    var counts: PostCount? = null,
    var content: PostContent? = null,
    @Json(name = "you_bookmarked") var youBookmarked: Boolean? = null,
    @Json(name = "you_reposted") var youReposted: Boolean? = null,
    @Json(name = "pagination_id") override var paginationId: String? = null,
    @Json(name = "raw") var raw: List<Raw<*>>? = null,
    @Json(name = "bookmarked_by") val bookmarkedBy: List<User>? = null,
    @Json(name = "reposted_by") val repostedBy: List<User>? = null
) : UniquePageable, Parcelable {
    @IgnoredOnParcel
    override val uniqueKey: String by lazy { id }

    @Parcelize
    data class PostContent(
        override var text: String? = null,
        override var html: String? = null,
        override var entities: Entities? = null,
        @Json(name = "link_not_parsed") var linksNotParsed: Boolean? = null
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
    @IgnoredOnParcel
    val canonicalUrl by lazy {
        "https://posts.pnut.io/$id"
    }

    @IgnoredOnParcel
    val isDeletedNonNull = isDeleted == true
    @IgnoredOnParcel
    val pollNotice = PollNotice.findPollNotice(raw)
    @IgnoredOnParcel
    @Transient
    var pollLastUpdate: Calendar? = null
    @IgnoredOnParcel
    var isPollNeedUpdate: Boolean = true
        get() {
            if (pollNotice == null) return false
            LogUtil.e("in post model: isPollNeedUpdate $pollLastUpdate")
            val res = (pollLastUpdate?.apply {
                add(Calendar.MINUTE, 1)
            })?.time?.let { it < Calendar.getInstance().time } ?: true
            pollLastUpdate = Calendar.getInstance()
            return res
        }
    @IgnoredOnParcel
    var poll: Poll? = null
    @IgnoredOnParcel
    val pollOptionsAdapter by lazy {
        pollNotice?.let { PollOptionsAdapter(it.value, poll) }
    }
}
