package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetCachedInteractionListOutputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

class GetCachedInteractionListUseCase(private val pnutCacheRepository: IPnutCacheRepository) :
    AsyncUseCase<GetCachedInteractionListOutputData, Unit>() {

    override suspend fun run(params: Unit): GetCachedInteractionListOutputData {
        val interactions = pnutCacheRepository.getInteractions()
        return GetCachedInteractionListOutputData(interactions)
    }
}