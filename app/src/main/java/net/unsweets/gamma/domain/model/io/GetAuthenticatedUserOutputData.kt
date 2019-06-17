package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.entity.User

data class GetAuthenticatedUserOutputData(
    val token: Token
)