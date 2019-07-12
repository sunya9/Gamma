package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.LogoutOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository

class LogoutUseCase(private val accountRepository: IAccountRepository, val pnutRepository: IPnutRepository) :
    UseCase<LogoutOutputData, Unit>() {
    override fun run(params: Unit): LogoutOutputData {
        val currentAccount = accountRepository.getDefaultAccount() ?: return LogoutOutputData(null)
        accountRepository.deleteAccount(currentAccount.id)
        val accountIdList = accountRepository.getStoredIds().filterNot { it == currentAccount.id }
        return when (accountIdList.isEmpty()) {
            true -> LogoutOutputData(null)
            false -> {
                val anotherAccountId = accountIdList.first()
                val anotherAccount = accountRepository.getAccount(anotherAccountId)
                    ?: return LogoutOutputData(null)
                accountRepository.updateDefaultAccount(anotherAccountId)
                pnutRepository.updateDefaultPnutService(anotherAccount.token)
                LogoutOutputData(anotherAccountId)
            }
        }
    }

}