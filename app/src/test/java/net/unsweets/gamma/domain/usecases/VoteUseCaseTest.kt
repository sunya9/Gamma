package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.domain.model.io.VoteInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Polls
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Assert
import org.junit.Test

class VoteUseCaseTest {
    private fun getVoteUseCase(vararg polls: Poll): VoteUseCase {
        val mockData = PnutRepositoryMock.PnutMockData(polls = listOf(*polls))
        val db = PnutRepositoryMock(mockData)
        return VoteUseCase(db)
    }

    @Test
    fun succeed() {
        val poll = Polls.poll1
        val voteUseCase = getVoteUseCase(poll)
        val input = VoteInputData(poll.id, poll.pollToken, setOf(0, 1, 2))
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

    @Test(expected = NoSuchElementException::class)
    fun notFound() {
        val voteUseCase = getVoteUseCase()
        val input = VoteInputData("", "", setOf(0))
        runBlocking { voteUseCase.run(input) }
    }
}