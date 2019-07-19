package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.UpdateUserImageInputData
import net.unsweets.gamma.domain.model.io.UpdateUserImageOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class UpdateUserImageUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<UpdateUserImageOutputData, UpdateUserImageInputData>() {
    override suspend fun run(params: UpdateUserImageInputData): UpdateUserImageOutputData {
        val res = when (params.type) {
            UpdateUserImageInputData.Type.Avatar -> pnutRepository.updateAvatar(params.uri)
            UpdateUserImageInputData.Type.Cover -> pnutRepository.updateCover(params.uri)
        }
        return UpdateUserImageOutputData(res)
    }

}