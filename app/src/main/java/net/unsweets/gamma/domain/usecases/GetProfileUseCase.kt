package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetProfileInputData
import net.unsweets.gamma.domain.model.io.GetProfileOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetProfileUseCase(val pnutRepository: IPnutRepository) :
    AsyncUseCase<GetProfileOutputData, GetProfileInputData>() {
    override suspend fun run(params: GetProfileInputData): GetProfileOutputData {
        val user = pnutRepository.getUserProfile(params.userId)
        return GetProfileOutputData(user)
    }
}
