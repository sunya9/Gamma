package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.StarInputData
import net.unsweets.gamma.domain.model.StarOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class StarUseCase(val pnutRepository: IPnutRepository) : UseCase<StarOutputData, StarInputData>() {
    override fun run(params: StarInputData): StarOutputData {
        val res = when (params.newState) {
            true -> pnutRepository.createStarPostSync(params.postId)
            false -> pnutRepository.deleteStarPostSync(params.postId)
        }
        return StarOutputData(res)
    }

}
