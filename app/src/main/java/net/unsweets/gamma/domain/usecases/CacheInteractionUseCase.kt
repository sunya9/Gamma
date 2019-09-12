package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.CacheInteractionInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

class CacheInteractionUseCase(private val pnutCacheRepository: IPnutCacheRepository) :
    AsyncUseCase<Unit, CacheInteractionInputData>() {
    override suspend fun run(params: CacheInteractionInputData) {
        pnutCacheRepository.storeInteractions(params.list)
    }
}
