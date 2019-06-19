package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.GetCurrentUserIdOutputData
import net.unsweets.gamma.domain.repository.IPreferenceRepository

class GetCurrentUserIdUseCase(val preferenceRepository: IPreferenceRepository) :
    UseCase<GetCurrentUserIdOutputData, Unit>() {
    override fun run(params: Unit): GetCurrentUserIdOutputData {
        val id = preferenceRepository.getDefaultAccountID()
        return GetCurrentUserIdOutputData(id ?: "")
    }

}