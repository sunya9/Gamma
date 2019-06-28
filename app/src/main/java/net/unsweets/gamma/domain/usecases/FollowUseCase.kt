package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.FollowInputData
import net.unsweets.gamma.domain.model.io.FollowOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class FollowUseCase(val pnutRepository: IPnutRepository) : AsyncUseCase<FollowOutputData, FollowInputData>() {
    override suspend fun run(params: FollowInputData): FollowOutputData {
        val res = when (params.newState) {
            true -> pnutRepository.follow(params.userId)
            else -> pnutRepository.unFollow(params.userId)
        }
        return FollowOutputData(res)
    }
}