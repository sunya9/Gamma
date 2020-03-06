package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetPollInputData
import net.unsweets.gamma.domain.model.io.GetPollOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

open class GetPollUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<GetPollOutputData, GetPollInputData>() {
    override suspend fun run(params: GetPollInputData): GetPollOutputData {
        val (pollId, pollToken) = params
        val res = pnutRepository.getPoll(pollId, pollToken)
        return GetPollOutputData(res.data)
    }
}