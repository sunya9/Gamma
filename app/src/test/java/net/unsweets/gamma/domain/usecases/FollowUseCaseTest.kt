package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.FollowInputData
import net.unsweets.gamma.mock.Mocks
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class FollowUseCaseTest {
    private val followUseCase = FollowUseCase(Mocks.pnutRepository)
    @Test
    fun succeedToFollow() {
        val input = FollowInputData("2", true)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(true))
    }

    @Test(expected = Exception::class)
    fun failToFollow() {
        val input = FollowInputData("3", true)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToUnFollow() {
        val input = FollowInputData("3", false)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(false))
    }

    @Test(expected = Exception::class)
    fun failToUnFollow() {
        val input = FollowInputData("2", false)
        runBlocking { followUseCase.run(input) }
    }
}