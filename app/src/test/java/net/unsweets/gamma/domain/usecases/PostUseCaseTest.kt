package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.model.io.PostInputData
import net.unsweets.gamma.mock.Mocks
import net.unsweets.gamma.util.ErrorCollections
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class PostUseCaseTest {
    private val postUseCase = PostUseCase(Mocks.pnutRepository, Mocks.accountRepository)
    @Test
    fun succeed() {
        val postBody = PostBody("body")
        val input = PostInputData(postBody, "1")
        val output = postUseCase.run(input)
        Assert.assertThat(output.res.data.content?.text, `is`("body"))
    }

    @Test(expected = Exception::class)
    fun failBecauseBodyIsEmpty() {
        val postBody = PostBody("")
        val input = PostInputData(postBody, "1")
        postUseCase.run(input)
    }

    @Test(expected = ErrorCollections.AccountNotFound::class)
    fun failBecauseBodyAccountNotFound() {
        val postBody = PostBody("")
        val input = PostInputData(postBody, "")
        postUseCase.run(input)
    }
}