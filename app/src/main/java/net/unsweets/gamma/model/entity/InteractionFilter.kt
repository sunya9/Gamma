package net.unsweets.gamma.model.entity

import com.squareup.moshi.Json

enum class InteractionFilter {
    @Json(name = "bookmark")
    Bookmark,
    @Json(name = "repost")
    Repost,
    @Json(name = "reply")
    Reply
}
