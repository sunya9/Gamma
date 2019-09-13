package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.CacheUserInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository

class CacheUserUseCase(
    private val pnutCacheRepository: IPnutCacheRepository,
    private val preferenceRepository: IPreferenceRepository
) :
    AsyncUseCase<Unit, CacheUserInputData>() {
    override suspend fun run(params: CacheUserInputData) {
        pnutCacheRepository.storeUsers(
            params.list,
            params.userListType,
            preferenceRepository.cacheSize
        )
    }
}
