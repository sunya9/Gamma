package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.SetupTokenOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository

class SetupTokenUseCase(
    private val pnutRepository: IPnutRepository,
    private val accountRepository: IAccountRepository,
    private val preferenceRepository: IPreferenceRepository
) :
    AsyncUseCase<SetupTokenOutputData, Unit>() {
    override suspend fun run(params: Unit): SetupTokenOutputData {

        val account = accountRepository.getDefaultAccount() ?: return SetupTokenOutputData(
            false
        )
        pnutRepository.updateDefaultPnutService(account.token)
        return SetupTokenOutputData(true)
    }
}