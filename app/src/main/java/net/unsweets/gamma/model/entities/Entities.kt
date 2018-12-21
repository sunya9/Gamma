package net.unsweets.gamma.model.entities

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Entities(
    val links: List<SealedEntity.LinkEntities>,
    val mentions: List<SealedEntity.MentionEntities>,
    val tags: List<SealedEntity.TagEntities>
) : Parcelable {
    sealed class SealedEntity : BaseEntities, Parcelable {
        @Parcelize
        data class LinkEntities(
            override val text: String,
            override val len: Int,
            override val pos: Int,
            val link: String,
            val title: String?,
            val description: String?
        ) : SealedEntity()

        @Parcelize
        data class MentionEntities(
            override val text: String,
            override val len: Int,
            override val pos: Int,
            val id: String,
            @Json(name = "is_leading") val isLeading: Boolean?,
            @Json(name = "is_copy") val isCopy: Boolean?
        ) : SealedEntity()

        @Parcelize
        data class TagEntities(
            override val len: Int,
            override val pos: Int,
            override val text: String
        ) : SealedEntity()
    }

    interface BaseEntities {
        val len: Int
        val pos: Int
        val text: String
    }
}