package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetCachedUserListInputData
import net.unsweets.gamma.domain.model.io.GetCachedUserListOutputData
import net.unsweets.gamma.domain.repository.IPnutCacheRepository

class GetCachedUserListUseCase(private val pnutCacheRepository: IPnutCacheRepository) :
    AsyncUseCase<GetCachedUserListOutputData, GetCachedUserListInputData>() {
    override suspend fun run(params: GetCachedUserListInputData): GetCachedUserListOutputData {
        val users = pnutCacheRepository.getUsers(params.userListType)
        return GetCachedUserListOutputData(users)
    }
}