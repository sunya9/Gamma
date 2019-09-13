package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.CacheInteractionInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository

class CacheInteractionUseCase(
    private val pnutCacheRepository: IPnutCacheRepository,
    private val preferenceRepository: IPreferenceRepository
) :
    AsyncUseCase<Unit, CacheInteractionInputData>() {
    override suspend fun run(params: CacheInteractionInputData) {
        pnutCacheRepository.storeInteractions(params.list, preferenceRepository.cacheSize)
    }
}
