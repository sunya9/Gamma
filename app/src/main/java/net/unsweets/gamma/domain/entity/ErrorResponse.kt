package net.unsweets.gamma.domain.entity

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.unsweets.gamma.R

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val meta: Meta
) {
    @JsonClass(generateAdapter = true)
    data class Meta(
        val code: Int,
        @Json(name = "error_message") val errorMessage: String
    ) {
        fun getResourceMessage(context: Context?): String {
            return getResource(errorMessage)?.let { context?.getString(it) } ?: errorMessage
        }
    }

    companion object Message2Resource {
        private val resourceMap = mapOf(
            "Post not bookmarked." to R.string.post_not_bookmarked,
            "No repost found to delete." to R.string.no_repost_found_to_delete,
            "Avatar must be less than 2MiB, with Content-Length specified." to R.string.avatar_over_size
        )

        fun getResource(message: String): Int? {
            return resourceMap[message]
        }
    }
}

