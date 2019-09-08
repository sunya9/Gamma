package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.GeneralUserParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.model.params.single.SearchUserParam

open class GetUsersParam(existParams: Map<String, String>? = null) : BaseComposeParam(existParams) {
    fun add(paginationParam: PaginationParam) = queryList.add(paginationParam)
    fun add(generalUserParam: GeneralUserParam) = queryList.add(generalUserParam)
    fun add(searchUserParam: SearchUserParam) = queryList.add(searchUserParam)
}
