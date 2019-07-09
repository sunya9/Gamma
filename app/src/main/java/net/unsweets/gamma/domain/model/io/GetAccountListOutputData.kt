package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.model.Account


data class GetAccountListOutputData(
    val accounts: List<Account>
)
