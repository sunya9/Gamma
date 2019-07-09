package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetAccountListOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository

class GetAccountListUseCase(private val accountRepository: IAccountRepository) :
    UseCase<GetAccountListOutputData, Unit>() {
    override fun run(params: Unit): GetAccountListOutputData {
        val accounts = accountRepository.getStoredIds().mapNotNull { accountRepository.getAccount(it) }
        return GetAccountListOutputData(accounts)
    }

}