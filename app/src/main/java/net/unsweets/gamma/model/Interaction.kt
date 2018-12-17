package net.unsweets.gamma.model

import com.squareup.moshi.Json
import net.unsweets.gamma.model.raw.PollNotice
import java.util.*

sealed class Interaction(
    @Json(name = "pagination_id")
    open val paginationId: String,
    @Json(name = "event_date")
    open val eventDate: Date,
    open val action: Action,
    open val users: List<User>
) {
    data class Repost(
        override val paginationId: String,
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : Interaction(paginationId, eventDate, action, users)
    data class Bookmark(
        override val paginationId: String,
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : Interaction(paginationId, eventDate, action, users)

    data class Reply(
        override val paginationId: String,
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : Interaction(paginationId, eventDate, action, users)

    data class Follow(
        override val paginationId: String,
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<User>
    ) : Interaction(paginationId, eventDate, action, users)

    data class PollResponse(
        override val paginationId: String,
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<PollNotice>
    ) : Interaction(paginationId, eventDate, action, users)

    enum class Action {
        @Json(name = "repost")
        Repost,
        @Json(name = "bookmark")
        Bookmark,
        @Json(name = "reply")
        Reply,
        @Json(name = "follow")
        Follow,
        @Json(name = "poll_response")
        PollResponse
    }

}
