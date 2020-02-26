package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.domain.model.io.GetPollInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Polls
import net.unsweets.gamma.util.Response
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class GetPollUseCaseTest {
  @Test
  fun success() {
    val poll = Polls.poll1
    val useCase = GetPollUseCase(object : PnutRepositoryMock() {
      override suspend fun getPoll(pollId: String, pollToken: String): PnutResponse<Poll> {
        return Response.success(poll)
      }
    })
    val res = runBlocking { useCase.run(GetPollInputData("1", "pollToken")) }
    Assert.assertThat(res.poll, `is`(poll))
  }
}