package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.VerifyTokenInputData
import net.unsweets.gamma.domain.model.io.VerifyTokenOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.PnutRepository

class VerifyTokenUseCase(
    private val accountRepository: IAccountRepository,
    private val pnutRepository: IPnutRepository
) : AsyncUseCase<VerifyTokenOutputData, VerifyTokenInputData>() {
    override suspend fun run(params: VerifyTokenInputData): VerifyTokenOutputData {
        val token = params.token
        val res = pnutRepository.verifyToken(token)
        accountRepository.addAccount(res.data.user.id, token)
        if(pnutRepository is PnutRepository) pnutRepository.updateDefaultPnutService(token)
        return VerifyTokenOutputData(res.data)
    }
}