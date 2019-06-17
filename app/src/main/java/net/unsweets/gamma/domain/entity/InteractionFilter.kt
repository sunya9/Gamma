package net.unsweets.gamma.domain.entity

import com.squareup.moshi.Json

enum class InteractionFilter {
    @Json(name = "bookmark")
    Bookmark,
    @Json(name = "repost")
    Repost,
    @Json(name = "reply")
    Reply
}
