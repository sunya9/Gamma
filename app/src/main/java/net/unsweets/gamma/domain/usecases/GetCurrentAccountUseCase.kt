package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.GetCurrentAccountOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository

class GetCurrentAccountUseCase(private val accountRepository: IAccountRepository) :
    UseCase<GetCurrentAccountOutputData, Unit>() {
    override fun run(params: Unit): GetCurrentAccountOutputData {
        val account = accountRepository.getDefaultAccount()
        return GetCurrentAccountOutputData(account)
    }
}