package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.UserListType

data class CacheUserInputData(
    val list: List<PageableItemWrapper<User>>,
    val userListType: UserListType
)
