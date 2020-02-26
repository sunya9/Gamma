package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.model.io.UpdateDefaultAccountInputData
import net.unsweets.gamma.mock.AccountRepositoryMock
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Accounts
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class UpdateDefaultAccountUseCaseTest {
  @Test
  fun success() {
    val account = Accounts.account
    val useCase = UpdateDefaultAccountUseCase(object : AccountRepositoryMock() {
      override fun updateDefaultAccount(id: String) {
        Assert.assertThat(id, `is`(account.id))
      }

      override fun getToken(id: String): String? {
        return account.token
      }
    }, object : PnutRepositoryMock() {
      override fun updateDefaultPnutService(token: String) {
        Assert.assertThat(token, `is`(account.token))
      }
    })
    val res = useCase.run(UpdateDefaultAccountInputData(account.id))
    Assert.assertThat(res.result, `is`(true))
  }

  @Test
  fun failed() {
    val account = Accounts.account
    val useCase = UpdateDefaultAccountUseCase(object : AccountRepositoryMock() {
      override fun updateDefaultAccount(id: String) {
        Assert.assertThat(id, `is`(account.id))
      }

      override fun getToken(id: String): String? {
        return null
      }
    }, object : PnutRepositoryMock() {
      override fun updateDefaultPnutService(token: String) {
        Assert.assertThat(token, `is`(account.token))
      }
    })
    val res = useCase.run(UpdateDefaultAccountInputData(account.id))
    Assert.assertThat(res.result, `is`(false))
  }
}