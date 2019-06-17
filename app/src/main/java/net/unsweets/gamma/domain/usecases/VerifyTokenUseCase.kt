package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.VerifyTokenInputData
import net.unsweets.gamma.domain.model.io.VerifyTokenOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.domain.repository.PnutRepository

class VerifyTokenUseCase(private val pnutRepository: IPnutRepository, private val preferenceRepository: IPreferenceRepository): UseCase<VerifyTokenOutputData, VerifyTokenInputData>() {
    override suspend fun run(params: VerifyTokenInputData): VerifyTokenOutputData {
        val token = params.token
        val res = pnutRepository.verifyToken(token)
        preferenceRepository.setDefaultAccount(res.data.user.id, token)
        if(pnutRepository is PnutRepository) pnutRepository.updateDefaultPnutService(token)
        return VerifyTokenOutputData(res.data)
    }
}