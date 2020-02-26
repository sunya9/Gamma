package net.unsweets.gamma.domain.usecases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.Client
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Token
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.GetAuthenticatedUserInputData
import net.unsweets.gamma.mock.PnutCacheRepositoryMock
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Users
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito


class GetAuthenticatedUserUseCaseTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun success() {
    val me = Users.me
    val cachedToken = Token(Client("testClient", "", ""), emptyList(), me, Token.Storage(0, 0))
    val latestToken = cachedToken.copy(user = cachedToken.user.copy(type = User.AccountType.BOT))
    val pnutRepository = object : PnutRepositoryMock() {
      override suspend fun getToken(): PnutResponse<Token> {
        return PnutResponse(PnutResponse.Meta(200), latestToken)
      }
    }
    val pnutCacheRepository = object : PnutCacheRepositoryMock() {
      override suspend fun getToken(): Token? {
        return cachedToken
      }

      override suspend fun storeToken(token: Token) {
        Assert.assertThat(token, `is`(latestToken))
      }
    }
    val liveData = Mockito.mock(MutableLiveData<Token>()::class.java)
    val useCase = GetAuthenticatedUserUseCase(pnutRepository, pnutCacheRepository)
    val res = runBlocking { useCase.run(GetAuthenticatedUserInputData(liveData)) }
    Mockito.verify(liveData, Mockito.times(2)).postValue(Mockito.any())
  }
}