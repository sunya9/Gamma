package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.VoteInputData
import net.unsweets.gamma.mock.Mocks
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Test

class VoteUseCaseTest {
    private val voteUseCase = VoteUseCase(Mocks.pnutRepository)
    @Test
    fun succeed() {
        val input = VoteInputData("1", "token", setOf(0, 1, 2))
        val output = runBlocking { voteUseCase.run(input) }
        Assert.assertThat(output.poll.options[0].isYourResponse, `is`(true))
        Assert.assertThat(output.poll.options[1].isYourResponse, `is`(true))
        Assert.assertThat(output.poll.options[2].isYourResponse, `is`(true))
        Assert.assertThat(output.poll.options[4].isYourResponse, not(true))
        Assert.assertThat(output.poll.options[4].isYourResponse, not(true))
        Assert.assertThat(output.poll.options[5].isYourResponse, not(true))
        Assert.assertThat(output.poll.options[6].isYourResponse, not(true))
        Assert.assertThat(output.poll.options[7].isYourResponse, not(true))
        Assert.assertThat(output.poll.options[8].isYourResponse, not(true))
        Assert.assertThat(output.poll.options[9].isYourResponse, not(true))
    }

    @Test(expected = Exception::class)
    fun notFound() {
        val input = VoteInputData("", "token", setOf(0))
        runBlocking { voteUseCase.run(input) }
    }
}