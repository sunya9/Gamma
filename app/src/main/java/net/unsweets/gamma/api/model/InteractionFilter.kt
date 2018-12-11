package net.unsweets.gamma.api.model

import com.squareup.moshi.Json

enum class InteractionFilter {
    @Json(name = "bookmark")
    Bookmark,
    @Json(name = "repost")
    Repost,
    @Json(name = "reply")
    Reply
}
