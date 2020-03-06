package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.CachePostInputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.util.LogUtil

open class CachePostUseCase(
    private val pnutCacheRepository: IPnutCacheRepository,
    private val preferenceRepository: IPreferenceRepository
) :
    AsyncUseCase<Unit, CachePostInputData>() {
    override suspend fun run(params: CachePostInputData) {
        LogUtil.e("CachePostUseCase")
        if (params.streamType == StreamType.Explore.MissedConversations) return
        pnutCacheRepository.storePosts(
            params.list,
            params.streamType,
            preferenceRepository.cacheSize
        )
    }
}
