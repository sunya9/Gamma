package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.PostInputData
import net.unsweets.gamma.domain.model.PostOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class PostUseCase(val pnutRepository: IPnutRepository) : UseCase<PostOutputData, PostInputData>() {
    override fun run(params: PostInputData): PostOutputData {
        val res = pnutRepository.createPostSync(params.postBody)
        return PostOutputData(res)
    }

}
