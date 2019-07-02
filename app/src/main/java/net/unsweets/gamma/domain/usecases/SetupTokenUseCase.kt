package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.SetupTokenOutputData
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.domain.service.IProvidePnutServiceService

class SetupTokenUseCase(
    private val providePnutServiceService: IProvidePnutServiceService,
    private val preferenceRepository: IPreferenceRepository
) :
    AsyncUseCase<SetupTokenOutputData, Unit>() {
    override suspend fun run(params: Unit): SetupTokenOutputData {

        val id = preferenceRepository.getDefaultAccountID() ?: return SetupTokenOutputData(
            false
        )
        val res = providePnutServiceService.updateDefaultPnutService(id)
        return SetupTokenOutputData(res)
    }
}