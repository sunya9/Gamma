package net.unsweets.gamma.api

data class PnutResponse<T>(val meta: Meta, val data: T) {
    data class Meta(
        val code: Int,
        val min_id: String?,
        val max_id: String?,
        val more: Boolean?
    )
}
