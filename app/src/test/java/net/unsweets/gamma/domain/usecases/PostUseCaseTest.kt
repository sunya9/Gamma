package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.PostBody
import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.model.io.PostInputData
import net.unsweets.gamma.mock.AccountRepositoryMock
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.util.ErrorCollections
import net.unsweets.gamma.util.RandomID
import net.unsweets.gamma.util.TestException
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class PostUseCaseTest {
    private val pnutRepositoryMockData = PnutRepositoryMock.PnutMockData()
    private val pnutRepository = PnutRepositoryMock(pnutRepositoryMockData)
    private val me = Account(RandomID.get, "valid token", "foo", "bar")
    private val accountRepository = AccountRepositoryMock(listOf(me))
    private val postUseCase = PostUseCase(pnutRepository, accountRepository)

    @Test
    fun succeed() {
        val postBody = PostBody("body")
        val input = PostInputData(postBody, me.id)
        val output = postUseCase.run(input)
        Assert.assertThat(output.res.data.content?.text, `is`("body"))
    }

    @Test(expected = TestException::class)
    fun failBecauseBodyIsEmpty() {
        val postBody = PostBody("")
        val input = PostInputData(postBody, me.id)
        postUseCase.run(input)
    }

    @Test(expected = ErrorCollections.AccountNotFound::class)
    fun failBecauseBodyAccountNotFound() {
        val postBody = PostBody("")
        val input = PostInputData(postBody, "")
        postUseCase.run(input)
    }
}