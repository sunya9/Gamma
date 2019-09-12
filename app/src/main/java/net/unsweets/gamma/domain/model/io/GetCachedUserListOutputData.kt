package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.CachedList

data class GetCachedUserListOutputData(
    val users: CachedList<User>
)
