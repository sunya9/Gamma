package net.unsweets.gamma.domain.entity

data class PnutResponse<T>(val meta: Meta, val data: T) {
    data class Meta(
        val code: Int,
        val min_id: String? = null,
        val max_id: String? = null,
        val more: Boolean? = null
    )
}
