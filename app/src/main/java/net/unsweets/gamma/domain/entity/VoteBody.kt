package net.unsweets.gamma.domain.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoteBody(
    val positions: List<Int>
)
