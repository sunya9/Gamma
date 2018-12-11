package net.unsweets.gamma.api.model

import com.squareup.moshi.Json

data class PostBody(
    val text: String,
    @Json(name = "reply_to") val replyTo: String? = null,
    @Json(name = "is_nsfw") val isNsfw: Boolean? = null,
    @Json(name = "entities.parse_links") val parseLinks: Boolean? = null,
    @Json(name = "entities.parse_markdown_links") val parseMarkdownLinks: Boolean? = null
)
