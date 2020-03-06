package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetAuthenticatedUserInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository
import net.unsweets.gamma.domain.repository.IPnutRepository

open class GetAuthenticatedUserUseCase(
    private val pnutRepository: IPnutRepository,
    private val pnutCacheRepository: IPnutCacheRepository
) : AsyncUseCase<Unit, GetAuthenticatedUserInputData>() {

    override suspend fun run(params: GetAuthenticatedUserInputData) {
        pnutCacheRepository.getToken()?.let {
            params.liveData.postValue(it)
        }
        val tokenRes = pnutRepository.getToken()
        pnutCacheRepository.storeToken(tokenRes.data)
        params.liveData.postValue(tokenRes.data)
    }

}
