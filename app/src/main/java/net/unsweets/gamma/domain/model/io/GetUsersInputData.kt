package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.model.UserListType
import net.unsweets.gamma.domain.model.params.composed.GetUsersParam

data class GetUsersInputData(
    val userListType: UserListType,
    val getUsersParam: GetUsersParam
)
