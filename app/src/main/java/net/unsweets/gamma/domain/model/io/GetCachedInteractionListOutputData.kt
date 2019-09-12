package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.model.CachedList

data class GetCachedInteractionListOutputData(
    val interactions: CachedList<Interaction>
)
