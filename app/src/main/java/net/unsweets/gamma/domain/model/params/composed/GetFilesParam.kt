package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.PaginationParam

class GetFilesParam(
    val includeIncomplete: Boolean = false
) : BaseComposeParam() {
    fun add(pageParams: PaginationParam) = queryList.add(pageParams)
}
