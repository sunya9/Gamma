package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.GeneralPostParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.model.params.single.SearchPostParam

open class GetPostsParam(existParams: Map<String, String>? = null) : BaseComposeParam(existParams) {
    init {
        add(GeneralPostParam(false))
    }
    fun add(pagination: PaginationParam) = queryList.add(pagination)
    fun add(generalPostParamParam: GeneralPostParam) = queryList.add(generalPostParamParam)
    fun add(searchPostParam: SearchPostParam) = queryList.add(searchPostParam)

}