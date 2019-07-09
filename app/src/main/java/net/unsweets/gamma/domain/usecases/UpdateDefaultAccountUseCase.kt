package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.UpdateDefaultAccountInputData
import net.unsweets.gamma.domain.model.io.UpdateDefaultAccountOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository

class UpdateDefaultAccountUseCase(private val accountRepository: IAccountRepository) :
    UseCase<UpdateDefaultAccountOutputData, UpdateDefaultAccountInputData>() {
    override fun run(params: UpdateDefaultAccountInputData): UpdateDefaultAccountOutputData {
        accountRepository.updateDefaultAccount(params.id)
        return UpdateDefaultAccountOutputData(true)
    }
}