package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetAuthenticatedUserOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository

class GetAuthenticatedUserUseCase(
    val pnutRepository: IPnutRepository,
    val preferenceRepository: IPreferenceRepository
) : AsyncUseCase<GetAuthenticatedUserOutputData, Unit>() {
    override suspend fun run(params: Unit): GetAuthenticatedUserOutputData {
        val tokenRes = pnutRepository.getToken()
        val token = tokenRes.data
        return GetAuthenticatedUserOutputData(token)
    }

}
