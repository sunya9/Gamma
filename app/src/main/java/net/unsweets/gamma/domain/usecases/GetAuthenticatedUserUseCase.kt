package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetAuthenticatedUserOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetAuthenticatedUserUseCase(
    val pnutRepository: IPnutRepository
) : AsyncUseCase<GetAuthenticatedUserOutputData, Unit>() {
    override suspend fun run(params: Unit): GetAuthenticatedUserOutputData {
        val tokenRes = pnutRepository.getToken()
        val token = tokenRes.data
        return GetAuthenticatedUserOutputData(token)
    }

}
