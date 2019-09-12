package net.unsweets.gamma.domain.model

import com.squareup.moshi.JsonClass
import net.unsweets.gamma.domain.entity.UniquePageable

@JsonClass(generateAdapter = true)
data class CachedList<T : UniquePageable>(
    val data: List<PageableItemWrapper<T>>
)