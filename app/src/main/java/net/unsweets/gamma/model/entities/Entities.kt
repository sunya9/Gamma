package net.unsweets.gamma.model.entities

import com.squareup.moshi.Json

data class Entities(
    val links: List<LinkEntities>,
    val mentions: List<MentionEntities>,
    val tags: List<TagEntities>
) {
    data class LinkEntities(
        override val text: String,
        override val len: Int,
        override val pos: Int,
        val link: String,
        val title: String?,
        val description: String?
    ) : BaseEntities

    data class MentionEntities(
        override val text: String,
        override val len: Int,
        override val pos: Int,
        val id: String,
        @Json(name = "is_leading") val isLeading: Boolean?,
        @Json(name ="is_copy") val isCopy: Boolean?
    ) : BaseEntities


    data class TagEntities(
        override val len: Int,
        override val pos: Int,
        override val text: String
    ) : BaseEntities

    interface BaseEntities {
        val len: Int
        val pos: Int
        val text: String
    }
}