package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.entity.PnutResponse

data class GetInteractionOutputData(
    val res: PnutResponse<List<Interaction>>
)
