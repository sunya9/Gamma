package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.CachePostInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.util.LogUtil

class CachePostUseCase(
    private val pnutCacheRepository: IPnutCacheRepository,
    private val preferenceRepository: IPreferenceRepository
) :
    AsyncUseCase<Unit, CachePostInputData>() {
    override suspend fun run(params: CachePostInputData) {
        LogUtil.e("CachePostUseCase")
        pnutCacheRepository.storePosts(
            params.list,
            params.streamType,
            preferenceRepository.cacheSize
        )
    }
}
