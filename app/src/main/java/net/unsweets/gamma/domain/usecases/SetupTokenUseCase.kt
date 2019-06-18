package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.SetupTokenOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.domain.repository.PnutRepository

class SetupTokenUseCase(val pnutRepository: IPnutRepository, val preferenceRepository: IPreferenceRepository) :
    AsyncUseCase<SetupTokenOutputData, Unit>() {
    override suspend fun run(params: Unit): SetupTokenOutputData {
        val token = preferenceRepository.getDefaultAccountToken() ?: return SetupTokenOutputData(
            false
        )
        if(pnutRepository is PnutRepository) pnutRepository.updateDefaultPnutService(token)
        return SetupTokenOutputData(true)
    }
}