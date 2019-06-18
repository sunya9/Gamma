package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetInteractionInputData
import net.unsweets.gamma.domain.model.io.GetInteractionOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetInteractionUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<GetInteractionOutputData, GetInteractionInputData>() {
    override suspend fun run(params: GetInteractionInputData): GetInteractionOutputData {
        val res = pnutRepository.getInteractions(params.getInteractionParam)
        return GetInteractionOutputData(res)
    }

}
