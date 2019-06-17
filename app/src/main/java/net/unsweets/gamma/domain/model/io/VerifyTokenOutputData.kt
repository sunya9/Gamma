package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.entity.User

data class VerifyTokenOutputData(
    val userData: Token
)