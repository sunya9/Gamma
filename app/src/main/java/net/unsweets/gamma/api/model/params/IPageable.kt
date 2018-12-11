package net.unsweets.gamma.api.model.params

import com.squareup.moshi.Json

interface IPageable {
    @Json(name = "min_id")
    val minId: String?
    @Json(name = "max_id")
    val maxId: String?
    val count: Int?
}