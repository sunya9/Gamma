package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.model.io.UpdateProfileInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.sample.Users
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class UpdateProfileUseCaseTest {
    private val me = Users.me
    private val mockData = PnutRepositoryMock.PnutMockData(users = listOf(me))
    private val db = PnutRepositoryMock(mockData)
    private val updateProfileUseCase = UpdateProfileUseCase(db)

    @Test
    fun succeed() {
        val input = UpdateProfileInputData("foo", "bar", "Asia/Tokyo", "ja_JP")
        val output = runBlocking { updateProfileUseCase.run(input) }
        val user = output.user
        Assert.assertThat(user.name, `is`("foo"))
        Assert.assertThat(user.content.text, `is`("bar"))
        Assert.assertThat(user.timezone, `is`("Asia/Tokyo"))
        Assert.assertThat(user.locale, `is`("ja_JP"))
    }

    @Test
    fun clearAll() {
        val input = UpdateProfileInputData("", "", "", "")
        val output = runBlocking { updateProfileUseCase.run(input) }
        val user = output.user
        Assert.assertThat(user.name, `is`(""))
        Assert.assertThat(user.content.text, `is`(""))
        Assert.assertThat(user.timezone, `is`(""))
        Assert.assertThat(user.locale, `is`(""))
    }
}