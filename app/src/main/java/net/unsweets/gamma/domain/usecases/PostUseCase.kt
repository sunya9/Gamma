package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.PostInputData
import net.unsweets.gamma.domain.model.io.PostOutputData
import net.unsweets.gamma.domain.repository.IAccountRepository
import net.unsweets.gamma.domain.repository.IPnutRepository
import net.unsweets.gamma.util.ErrorCollections

class PostUseCase(
    private val pnutRepository: IPnutRepository,
    private val accountRepository: IAccountRepository
) :
    UseCase<PostOutputData, PostInputData>() {
    override fun run(params: PostInputData): PostOutputData {
        val token = accountRepository.getToken(params.accountId) ?: throw ErrorCollections.AccountNotFound
        val res = pnutRepository.createPostSync(params.postBody, token)
        return PostOutputData(res)
    }

}
