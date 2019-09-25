package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.GetProfileInputData
import net.unsweets.gamma.mock.Mocks
import net.unsweets.gamma.util.ErrorCollections
import net.unsweets.sample.Users
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class GetProfileUseCaseTest {
    private val getProfileUse = GetProfileUseCase(Mocks.pnutRepository)

    @Test
    fun succeed() {
        val input = GetProfileInputData("1")
        val output = runBlocking { getProfileUse.run(input) }
        Assert.assertThat(output.res.data, `is`(Users.getUser("1")))
    }

    @Test(expected = ErrorCollections.CommunicationError::class)
    fun fail() {
        val input = GetProfileInputData("")
        runBlocking { getProfileUse.run(input) }
    }
}