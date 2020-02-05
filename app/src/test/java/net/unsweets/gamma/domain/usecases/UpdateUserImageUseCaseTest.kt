package net.unsweets.gamma.domain.usecases

import android.net.Uri
import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.UpdateUserImageInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Users
import net.unsweets.gamma.util.Response
import net.unsweets.gamma.util.TestException
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Uri::class)
class UpdateUserImageUseCaseTest {
    private val me = Users.me
    private val dummyUri by lazy { Uri.parse("dummy") }

    @Before
    fun setup() {
        PowerMockito.mockStatic(Uri::class.java)
        val uri = Mockito.mock(Uri::class.java)
        PowerMockito.`when`<Uri>(Uri::class.java, "parse", ArgumentMatchers.anyString())
            .thenReturn(uri)
    }


    @Test
    fun succeedUpdateAvatar() {
        val pnutRepository = object : PnutRepositoryMock() {
            override suspend fun updateAvatar(uri: Uri): PnutResponse<User> {
                val user =
                    me.copy(content = me.content.copy(avatarImage = me.content.avatarImage.copy(link = "updated")))
                return Response.success(user)
            }
        }
        val updateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
        val res = runBlocking {
            updateUserImageUseCase.run(
                UpdateUserImageInputData(
                    dummyUri,
                    UpdateUserImageInputData.Type.Avatar
                )
            )
        }
        Assert.assertThat(res.res.data.content.avatarImage.link, `is`("updated"))
    }

    @Test(expected = TestException::class)
    fun failToUpdateAvatar() {
        val pnutRepository = object : PnutRepositoryMock() {
            override suspend fun updateAvatar(uri: Uri): PnutResponse<User> {
                throw TestException()
            }
        }
        val updateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
        runBlocking {
            updateUserImageUseCase.run(
                UpdateUserImageInputData(
                    dummyUri,
                    UpdateUserImageInputData.Type.Avatar
                )
            )
        }
    }

    @Test
    fun succeedUpdateCover() {
        val pnutRepository = object : PnutRepositoryMock() {
            override suspend fun updateCover(uri: Uri): PnutResponse<User> {
                val user =
                    me.copy(content = me.content.copy(coverImage = me.content.coverImage.copy(link = "updated")))
                return Response.success(user)
            }
        }
        val updateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
        val res = runBlocking {
            updateUserImageUseCase.run(
                UpdateUserImageInputData(
                    dummyUri,
                    UpdateUserImageInputData.Type.Cover
                )
            )
        }
        Assert.assertThat(res.res.data.content.coverImage.link, `is`("updated"))
    }

    @Test(expected = TestException::class)
    fun failToUpdateCover() {
        val pnutRepository = object : PnutRepositoryMock() {
            override suspend fun updateCover(uri: Uri): PnutResponse<User> {
                throw TestException()
            }
        }
        val updateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
        runBlocking {
            updateUserImageUseCase.run(
                UpdateUserImageInputData(
                    dummyUri,
                    UpdateUserImageInputData.Type.Cover
                )
            )
        }
    }

    @Test
    fun succeedToDeleteAvatar() {
        val pnutRepository = object : PnutRepositoryMock() {
            override suspend fun deleteAvatar(): PnutResponse<User> {
                val user =
                    me.copy(
                        content = me.content.copy(
                            avatarImage = me.content.avatarImage.copy(
                                link = "deleted",
                                isDefault = true
                            )
                        )
                    )
                return Response.success(user)
            }
        }
        val updateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
        val res = runBlocking {
            updateUserImageUseCase.run(
                UpdateUserImageInputData(
                    null,
                    UpdateUserImageInputData.Type.Avatar
                )
            )
        }
        Assert.assertThat(res.res.data.content.avatarImage.isDefault, `is`(true))
    }

    @Test
    fun succeedToDeleteCover() {
        val pnutRepository = object : PnutRepositoryMock() {
            override suspend fun deleteCover(): PnutResponse<User> {
                val user =
                    me.copy(
                        content = me.content.copy(
                            coverImage = me.content.coverImage.copy(
                                link = "deleted",
                                isDefault = true
                            )
                        )
                    )
                return Response.success(user)
            }
        }
        val updateUserImageUseCase = UpdateUserImageUseCase(pnutRepository)
        val res = runBlocking {
            updateUserImageUseCase.run(
                UpdateUserImageInputData(
                    null,
                    UpdateUserImageInputData.Type.Cover
                )
            )
        }
        Assert.assertThat(res.res.data.content.coverImage.isDefault, `is`(true))
    }

}