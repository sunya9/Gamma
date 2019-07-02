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

        val id = preferenceRepository.getDefaultAccountID() ?: return SetupTokenOutputData(
            false
        )
        val token = accountRepository.getToken(id) ?: return SetupTokenOutputData(false)
        pnutRepository.updateDefaultPnutService(token)
        return SetupTokenOutputData(true)
    }
}