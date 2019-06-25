package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.ProfileBody
import net.unsweets.gamma.domain.model.io.UpdateProfileInputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class UpdateProfileUseCase(val pnutRepository: IPnutRepository) : AsyncUseCase<Unit, UpdateProfileInputData>() {
    override suspend fun run(params: UpdateProfileInputData) {
        val profileBody =
            ProfileBody(params.name, ProfileBody.Content(params.description), params.timezone, params.locale)
        pnutRepository.updateMyProfile(profileBody)
    }

}
