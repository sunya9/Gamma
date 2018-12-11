package net.unsweets.gamma.model

import com.squareup.moshi.Json
import net.unsweets.gamma.model.raw.PollNotice
import java.util.*

object Interaction {
    data class Repost(
        @Json(name = "pagination_id") override val paginationId: String,
        @Json(name = "event_date") override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : IInteraction

    data class Bookmark(
        @Json(name = "pagination_id") override val paginationId: String,
        @Json(name = "event_date") override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : IInteraction

    data class Reply(
        @Json(name = "pagination_id") override val paginationId: String,
        @Json(name = "event_date") override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : IInteraction

    data class Follow(
        @Json(name = "pagination_id") override val paginationId: String,
        @Json(name = "event_date") override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<User>
    ) : IInteraction

    data class PollResponse(
        @Json(name = "pagination_id") override val paginationId: String,
        @Json(name = "event_date") override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<PollNotice>
    ) : IInteraction

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

    interface IInteraction {
        @Json(name = "pagination_id")
        val paginationId: String
        @Json(name = "event_date")
        val eventDate: Date
        val action: Action
        val users: List<User>
    }
}