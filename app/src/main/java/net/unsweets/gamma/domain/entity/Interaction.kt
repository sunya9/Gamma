package net.unsweets.gamma.domain.entity

import android.content.Context
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.raw.PollNotice
import java.util.*

sealed class Interaction(
    override val paginationId: String,
    open val eventDate: Date,
    open val action: Action,
    open val users: List<User>
) : Parcelable, Pageable {
    @Parcelize
    data class Repost(
        @Json(name = "pagination_id")
        override val paginationId: String,
        @Json(name = "event_date")
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : Interaction(paginationId, eventDate, action, users)
    @Parcelize
    data class Bookmark(
        @Json(name = "pagination_id")
        override val paginationId: String,
        @Json(name = "event_date")
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : Interaction(paginationId, eventDate, action, users)

    @Parcelize
    data class Reply(
        @Json(name = "pagination_id")
        override val paginationId: String,
        @Json(name = "event_date")
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<Post>
    ) : Interaction(paginationId, eventDate, action, users)

    @Parcelize
    data class Follow(
        @Json(name = "pagination_id")
        override val paginationId: String,
        @Json(name = "event_date")
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<User>
    ) : Interaction(paginationId, eventDate, action, users)

    @Parcelize
    data class PollResponse(
        @Json(name = "pagination_id")
        override val paginationId: String,
        @Json(name = "event_date")
        override val eventDate: Date,
        override val action: Action,
        override val users: List<User>,
        val objects: List<PollNotice>
    ) : Interaction(paginationId, eventDate, action, users)

    enum class Action(@DrawableRes val iconRes: Int, @StringRes val actionRes: Int) {
        @Json(name = "repost")
        Repost(R.drawable.ic_cached_black_24dp, R.string.action_reposted_by),
        @Json(name = "bookmark")
        Bookmark(R.drawable.ic_star_black_24dp, R.string.action_starred_by),
        @Json(name = "reply")
        Reply(R.drawable.ic_reply_black_24dp, R.string.action_replied_by),
        @Json(name = "follow")
        Follow(R.drawable.ic_person_add_black_24dp, R.string.action_followed_by),
        @Json(name = "poll_response")
        PollResponse(R.drawable.ic_poll_black_24dp, R.string.action_voted_by)
    }

    fun getMessage(context: Context): CharSequence {
        val actionStr = context.getString(action.actionRes)
        val usersStr = users.joinToString(", ") { "@${it.username}" }
        return context.getString(R.string.action_template_text, actionStr, usersStr)
    }
}
