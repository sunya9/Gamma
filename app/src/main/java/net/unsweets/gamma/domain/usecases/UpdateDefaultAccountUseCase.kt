package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.UpdateDefaultAccountInputData
import net.unsweets.gamma.domain.model.io.UpdateDefaultAccountOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository

class UpdateDefaultAccountUseCase(
    private val accountRepository: IAccountRepository,
    private val pnutRepository: IPnutRepository
) :
    UseCase<UpdateDefaultAccountOutputData, UpdateDefaultAccountInputData>() {
    override fun run(params: UpdateDefaultAccountInputData): UpdateDefaultAccountOutputData {
        accountRepository.updateDefaultAccount(params.id)
        val token = accountRepository.getToken(params.id) ?: return UpdateDefaultAccountOutputData(false)
        pnutRepository.updateDefaultPnutService(token)
        return UpdateDefaultAccountOutputData(true)
    }
}