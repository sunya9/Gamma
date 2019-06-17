package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.model.params.single.GeneralPostParam

open class GetPostsParam: BaseComposeParam() {
    init {
        add(GeneralPostParam(false))
    }
    fun add(pagination: PaginationParam) = queryList.add(pagination)
    fun add(generalPostParamParam: GeneralPostParam) = queryList.add(generalPostParamParam)
}