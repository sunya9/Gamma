package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.VerifyTokenInputData
import net.unsweets.gamma.mock.Mocks
import net.unsweets.sample.Users
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class VerifyTokenUseCaseTest {
    private val verifyTokenUseCase =
        VerifyTokenUseCase(Mocks.accountRepository, Mocks.pnutRepository)

    @Test
    fun succeed() {
        val input = VerifyTokenInputData("valid")
        val output = runBlocking { verifyTokenUseCase.run(input) }
        Assert.assertThat(output.userData.user, `is`(Users.getUser("1")))
    }

    @Test(expected = Exception::class)
    fun fail() {
        val input = VerifyTokenInputData("")
        runBlocking { verifyTokenUseCase.run(input) }
    }
}