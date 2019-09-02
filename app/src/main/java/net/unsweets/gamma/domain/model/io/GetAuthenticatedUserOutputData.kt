package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Token

data class GetAuthenticatedUserOutputData(
    val token: Token
)