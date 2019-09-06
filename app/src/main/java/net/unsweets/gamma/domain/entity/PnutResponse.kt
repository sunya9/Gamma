package net.unsweets.gamma.domain.entity

import com.squareup.moshi.Json

data class PnutResponse<T>(val meta: Meta, val data: T) {
    data class Meta(
        val code: Int,
        val min_id: String? = null,
        val max_id: String? = null,
        val more: Boolean? = null,
        @Json(name = "deleted_ids") val deletedIds: List<String>? = null,
        @Json(name = "revised_ids") val revisedIds: List<String>? = null
    )
}
