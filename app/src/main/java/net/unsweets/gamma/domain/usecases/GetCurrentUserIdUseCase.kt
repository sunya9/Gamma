package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetCurrentUserIdOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository

class GetCurrentUserIdUseCase(private val accountRepository: IAccountRepository) :
    UseCase<GetCurrentUserIdOutputData, Unit>() {
    override fun run(params: Unit): GetCurrentUserIdOutputData {
        val account = accountRepository.getDefaultAccount()
        return GetCurrentUserIdOutputData(account?.id ?: "")
    }
}