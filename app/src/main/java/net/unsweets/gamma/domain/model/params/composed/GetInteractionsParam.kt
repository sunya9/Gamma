package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.single.InteractionParam

class GetInteractionsParam: GetPostsParam() {
    init {
        add(InteractionParam())
    }
    fun add(interactionParam: InteractionParam) = queryList.add(interactionParam)
}