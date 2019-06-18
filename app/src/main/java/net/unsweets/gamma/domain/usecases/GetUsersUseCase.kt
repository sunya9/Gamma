package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.model.io.GetUsersInputData
import net.unsweets.gamma.domain.model.io.GetUsersOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetUsersUseCase(val pnutRepository: IPnutRepository) : AsyncUseCase<GetUsersOutputData, GetUsersInputData>() {
    override suspend fun run(params: GetUsersInputData): GetUsersOutputData {
        val res = when(params.userListType) {
            is UserListType.Followers -> pnutRepository.getFollowers(params.userId, params.getUsersParam)
            is UserListType.Following -> pnutRepository.getFollowing(params.userId, params.getUsersParam)
        }
        return GetUsersOutputData(res)
    }

}
