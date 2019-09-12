package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetCachedPostListInputData
import net.unsweets.gamma.domain.model.io.GetCachedPostListOutputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

class GetCachedPostListUseCase(private val pnutCacheRepository: IPnutCacheRepository) :
    AsyncUseCase<GetCachedPostListOutputData, GetCachedPostListInputData>() {
    override suspend fun run(params: GetCachedPostListInputData): GetCachedPostListOutputData {
        val posts = pnutCacheRepository.getPosts(params.streamType)
        return GetCachedPostListOutputData(posts)
    }
}