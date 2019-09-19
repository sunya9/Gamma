package net.unsweets.gamma.domain

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoteBody(
    val positions: List<Int>
)
