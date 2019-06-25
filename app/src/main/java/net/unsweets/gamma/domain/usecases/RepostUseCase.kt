package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.RepostInputData
import net.unsweets.gamma.domain.model.io.RepostOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class RepostUseCase(val pnutRepository: IPnutRepository) : UseCase<RepostOutputData, RepostInputData>() {
    override fun run(params: RepostInputData): RepostOutputData {
        val res = when (params.newState) {
            true -> pnutRepository.createRepostSync(params.postId)
            false -> pnutRepository.deleteRepostSync(params.postId)
        }
        return RepostOutputData(res)
    }

}
