package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.DeletePostInputData
import net.unsweets.gamma.domain.model.io.DeletePostOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class DeletePostUseCase(private val pnutRepository: IPnutRepository) :
    UseCase<DeletePostOutputData, DeletePostInputData>() {
    override fun run(params: DeletePostInputData): DeletePostOutputData {
        val res = pnutRepository.deletePost(params.postId)
        return DeletePostOutputData(res)
    }
}