package net.unsweets.gamma.domain.usecases

import android.util.Log
import net.unsweets.gamma.domain.model.io.SetupTokenOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository

open class SetupTokenUseCase(
    private val pnutRepository: IPnutRepository,
    private val accountRepository: IAccountRepository
) :
    AsyncUseCase<SetupTokenOutputData, Unit>() {
    override suspend fun run(params: Unit): SetupTokenOutputData {
      Log.e("SetupTokenUseCase", "run")

        val account = accountRepository.getDefaultAccount() ?: return SetupTokenOutputData(
            false
        )
        pnutRepository.updateDefaultPnutService(account.token)
        return SetupTokenOutputData(true)
    }
}