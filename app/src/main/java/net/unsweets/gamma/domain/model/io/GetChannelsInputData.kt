package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.model.params.composed.GetChannelsParam

data class GetChannelsInputData(
    val channelType: ChannelType,
    val params: GetChannelsParam
) {
    enum class ChannelType
}
