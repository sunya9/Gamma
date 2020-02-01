package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.GetProfileInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Users
import net.unsweets.gamma.util.ErrorCollections
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class GetProfileUseCaseTest {
    private val me = Users.me
    private val mockData = PnutRepositoryMock.PnutMockData(users = listOf(me))
    private val db = PnutRepositoryMock(mockData)
    private val getProfileUseCase = GetProfileUseCase(db)

    @Test
    fun succeed() {
        val input = GetProfileInputData(me.id)
        val output = runBlocking { getProfileUseCase.run(input) }
        Assert.assertThat(output.res.data, `is`(me))
    }

    @Test(expected = ErrorCollections.CommunicationError::class)
    fun fail() {
        val input = GetProfileInputData("")
        runBlocking { getProfileUseCase.run(input) }
    }
}