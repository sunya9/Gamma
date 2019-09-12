package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.CacheUserInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

class CacheUserUseCase(private val pnutCacheRepository: IPnutCacheRepository) :
    AsyncUseCase<Unit, CacheUserInputData>() {
    override suspend fun run(params: CacheUserInputData) {
        pnutCacheRepository.storeUsers(params.list, params.userListType)
    }
}
