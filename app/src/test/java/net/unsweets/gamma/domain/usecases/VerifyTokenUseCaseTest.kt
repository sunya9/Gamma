package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.Client
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.model.io.VerifyTokenInputData
import net.unsweets.gamma.mock.AccountRepositoryMock
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Users
import net.unsweets.gamma.util.RandomID
import net.unsweets.gamma.util.TestException
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class VerifyTokenUseCaseTest {
    private val me = Users.me
    private val validToken = "valid"
    private val pnutRepository = object : PnutRepositoryMock() {
        override suspend fun verifyToken(token: String): PnutResponse<Token> {
            return when (token) {
                validToken -> {
                    val resToken = Token(
                        Client("", "", ""),
                        emptyList(),
                        me,
                        Token.Storage(0, 0)
                    )
                    PnutResponse(PnutResponse.Meta(200), resToken)
                }
                else -> throw TestException()
            }
        }
    }
    private val myAccount = Account(RandomID.get, "access token", "foo", "bar")
    private val accountRepository = AccountRepositoryMock(listOf(myAccount))
    private val verifyTokenUseCase = VerifyTokenUseCase(accountRepository, pnutRepository)

    @Test
    fun succeed() {
        val input = VerifyTokenInputData(validToken)
        val output = runBlocking { verifyTokenUseCase.run(input) }
        Assert.assertThat(output.userData.user, `is`(me))
    }

    @Test(expected = Exception::class)
    fun fail() {
        val input = VerifyTokenInputData("")
        runBlocking { verifyTokenUseCase.run(input) }
    }
}