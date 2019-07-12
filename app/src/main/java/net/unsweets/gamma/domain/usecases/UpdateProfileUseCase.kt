package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.ProfileBody
import net.unsweets.gamma.domain.model.io.UpdateProfileInputData
import net.unsweets.gamma.domain.model.io.UpdateProfileOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class UpdateProfileUseCase(val pnutRepository: IPnutRepository) :
    AsyncUseCase<UpdateProfileOutputData, UpdateProfileInputData>() {
    override suspend fun run(params: UpdateProfileInputData): UpdateProfileOutputData {
        val profileBody =
            ProfileBody(params.name, ProfileBody.Content(params.description), params.timezone, params.locale)
        val user = pnutRepository.updateMyProfile(profileBody)
        return UpdateProfileOutputData(user.data)
    }

}
