package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.GeneralUserParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam

class GetUsersParam: BaseComposeParam() {
    fun add(paginationParam: PaginationParam) = queryList.add(paginationParam)
    fun add(generalUserParam: GeneralUserParam) = queryList.add(generalUserParam)
}
