package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.model.io.GetUsersInputData
import net.unsweets.gamma.domain.model.io.GetUsersOutputData
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam
import net.unsweets.gamma.domain.model.params.single.SearchUserParam
import net.unsweets.gamma.domain.repository.IPnutRepository

class GetUsersUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<GetUsersOutputData, GetUsersInputData>() {
    override suspend fun run(params: GetUsersInputData): GetUsersOutputData {
        val res = when (val userListType = params.userListType) {
            is UserListType.Followers -> pnutRepository.getFollowers(userListType.userId, params.getUsersParam)
            is UserListType.Following -> pnutRepository.getFollowing(userListType.userId, params.getUsersParam)
            is UserListType.Search -> pnutRepository.searchUsers(
                GetUsersParam(params.getUsersParam.toMap()).apply {
                    add(SearchUserParam(userListType.keyword))
                })
        }
        return GetUsersOutputData(res)
    }

}
