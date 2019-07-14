package net.unsweets.gamma.domain.entity

import android.net.Uri
import com.squareup.moshi.Json
import net.unsweets.gamma.domain.entity.raw.PostRaw
import java.io.Serializable

data class PostBody(
    val text: String,
    @Json(name = "reply_to") val replyTo: String? = null,
    @Json(name = "is_nsfw") val isNsfw: Boolean? = null,
    @Json(name = "entities.parse_links") val parseLinks: Boolean? = null,
    @Json(name = "entities.parse_markdown_links") val parseMarkdownLinks: Boolean? = null,
    val raw: List<PostRaw<*>> = emptyList(),
    val files: List<Uri> = emptyList()
) : Serializable
