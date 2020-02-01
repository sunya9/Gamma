package net.unsweets.gamma.util

import net.unsweets.gamma.domain.entity.PnutResponse

object Response {
    fun <T> success(data: T, meta: PnutResponse.Meta = PnutResponse.Meta(200)): PnutResponse<T> {
        return PnutResponse(meta, data)
    }
}