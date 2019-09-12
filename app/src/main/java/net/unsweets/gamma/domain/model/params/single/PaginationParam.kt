package net.unsweets.gamma.domain.model.params.single

import net.unsweets.gamma.domain.entity.UniquePageable
import net.unsweets.gamma.domain.model.PageableItemWrapper

data class PaginationParam(
    val beforeId: String? = null,
    val sinceId: String? = null,
    val count: Int? = null

) : BaseParam {
    override fun toMap(): Map<String, String> = hashMapOf<String, String>().also { map ->
        beforeId?.let { map["before_id"] = it }
        sinceId?.let { map["since_id"] = it }
        count?.let { map["count"] = it.toString() }
    }

    companion object {
        fun createFromPager(
            pager: PageableItemWrapper.Pager<out UniquePageable>
        ): PaginationParam {
            return PaginationParam(pager.getBeforeId(), pager.getSinceId())
        }
    }
}