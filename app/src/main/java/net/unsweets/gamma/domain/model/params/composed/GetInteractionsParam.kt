package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.InteractionParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam

class GetInteractionsParam : BaseComposeParam() {
    init {
        add(InteractionParam())
    }

    fun add(pagination: PaginationParam) = queryList.add(pagination)
    fun add(interactionParam: InteractionParam) = queryList.add(interactionParam)
}