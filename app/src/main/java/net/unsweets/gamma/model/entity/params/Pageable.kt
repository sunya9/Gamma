package net.unsweets.gamma.model.entity.params

import com.squareup.moshi.Json

data class Pageable(
    @Json(name = "min_id") override val minId: String? = null,
    @Json(name = "max_id") override val maxId: String? = null,
    override val count: Int? = null
) : IPageable