package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.CachedList
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.GetCachedPostListInputData
import net.unsweets.gamma.domain.model.io.GetCachedPostListOutputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

open class GetCachedPostListUseCase(private val pnutCacheRepository: IPnutCacheRepository) :
    AsyncUseCase<GetCachedPostListOutputData, GetCachedPostListInputData>() {
    override suspend fun run(params: GetCachedPostListInputData): GetCachedPostListOutputData {
        if (params.streamType == StreamType.Explore.MissedConversations) return GetCachedPostListOutputData(
            CachedList(emptyList())
        )
        val posts = pnutCacheRepository.getPosts(params.streamType)
        return GetCachedPostListOutputData(posts)
    }
}