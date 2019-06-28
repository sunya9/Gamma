package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.User

data class FollowOutputData(
    val res: PnutResponse<User>
)
