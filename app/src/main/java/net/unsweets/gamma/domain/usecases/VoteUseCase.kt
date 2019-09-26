package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.VoteBody
import net.unsweets.gamma.domain.model.io.VoteInputData
import net.unsweets.gamma.domain.model.io.VoteOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class VoteUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<VoteOutputData, VoteInputData>() {
    override suspend fun run(params: VoteInputData): VoteOutputData {
        val voteBody =
            VoteBody(params.positions.toList().map { it + 1 })
        val res = pnutRepository.vote(params.pollId, params.pollToken, voteBody)
        return VoteOutputData(res.data)
    }
}
