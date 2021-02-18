package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetChannelsInputData
import net.unsweets.gamma.domain.model.io.GetChannelsOutputData
import net.unsweets.gamma.domain.model.params.single.GeneralChannelParam
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetChannelsUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<GetChannelsOutputData, GetChannelsInputData>() {
    override suspend fun run(params: GetChannelsInputData): GetChannelsOutputData {
        val channels = pnutRepository.getChannels(params.params.add(GeneralChannelParam(includeLimitedUsers = true)))
        return GetChannelsOutputData(channels)
    }
}