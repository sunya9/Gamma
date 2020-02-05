package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Channel
import net.unsweets.gamma.domain.entity.PnutResponse

data class GetChannelsOutputData(
    val channels: PnutResponse<List<Channel>>
)
