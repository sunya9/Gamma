package net.unsweets.gamma.domain.model.params.single

import com.squareup.moshi.Json

data class PaginationParam(
    @Json(name = "min_id") val minId: String? = null,
    @Json(name = "max_id") val maxId: String? = null,
    val count: Int? = null

): BaseParam {
    override fun toMap(): Map<String, String> = hashMapOf<String, String>().also { map ->
        minId?.let { map["before_id"] = it }
        maxId?.let { map["since_id"] = it }
        count?.let { map["count"] = it.toString() }
    }
}