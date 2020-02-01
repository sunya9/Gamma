package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.UpdateUserImageInputData
import net.unsweets.gamma.domain.model.io.UpdateUserImageOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class UpdateUserImageUseCase(private val pnutRepository: IPnutRepository) :
    AsyncUseCase<UpdateUserImageOutputData, UpdateUserImageInputData>() {
    override suspend fun run(params: UpdateUserImageInputData): UpdateUserImageOutputData {
        val res = when (params.type) {
            UpdateUserImageInputData.Type.Avatar -> {
                if (params.uri != null) {
                    pnutRepository.updateAvatar(params.uri)
                } else {
                    pnutRepository.deleteAvatar()
                }
            }
            UpdateUserImageInputData.Type.Cover -> {
                if (params.uri != null) {
                    pnutRepository.updateCover(params.uri)
                } else {
                    pnutRepository.deleteCover()
                }
            }
        }
        return UpdateUserImageOutputData(res)
    }

}