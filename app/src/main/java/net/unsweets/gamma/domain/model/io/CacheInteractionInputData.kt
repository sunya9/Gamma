package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.model.PageableItemWrapper

data class CacheInteractionInputData(
    val list: List<PageableItemWrapper<Interaction>>
)
