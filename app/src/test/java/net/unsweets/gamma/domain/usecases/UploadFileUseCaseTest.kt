package net.unsweets.gamma.domain.usecases

import android.net.Uri
import net.unsweets.gamma.domain.entity.File
import net.unsweets.gamma.domain.entity.FileBody
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.raw.OEmbed
import net.unsweets.gamma.domain.model.UriInfo
import net.unsweets.gamma.domain.model.io.UploadFileInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Clients
import net.unsweets.gamma.sample.Users
import net.unsweets.gamma.util.Constants
import okhttp3.RequestBody
import okio.Buffer
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
import java.io.ByteArrayInputStream
import java.util.*

@RunWith(PowerMockRunner::class)
@PrepareForTest(Uri::class, java.io.File::class)

class UploadFileUseCaseTest {
  private val dummyUri by lazy { Uri.parse("dummy") }

  @Before
  fun setup() {
    PowerMockito.mockStatic(Uri::class.java)
    PowerMockito.mock(Uri::class.java)
    val uri = Mockito.mock(Uri::class.java)
    PowerMockito.mockStatic(java.io.File::class.java)
    PowerMockito.`when`<Uri>(Uri::class.java, "parse", ArgumentMatchers.anyString())
      .thenReturn(uri)
    PowerMockito.`when`<String>(dummyUri, "getPath").thenReturn("dummyPath")
  }

  @Test
  fun upload() {
    val file = File(
      createdAt = Date(),
      id = "1",
      isComplete = true,
      isPublic = true,
      kind = File.FileKind.OTHER,
      name = "testFile.txt",
      sha256 = "sha256",
      size = 1,
      source = Clients.testClient,
      type = Constants.Gamma,
      user = Users.me,
      fileToken = "fileToken"
    )
    val useCase = UploadFileUseCase(object : PnutRepositoryMock() {
      override fun createFile(content: RequestBody, fileBody: FileBody): PnutResponse<File> {
        val buffer = Buffer()
        content.writeTo(buffer)
        // TODO: Fix it
//        Assert.assertThat(fileBody.kind, `is`(File.FileKind.OTHER))
        Assert.assertThat(fileBody.name, `is`("dummyPath"))
        Assert.assertThat(buffer.readUtf8(), `is`("test utf8 data"))
        return PnutResponse(PnutResponse.Meta(200), file)
      }
    })
    val inputStream = ByteArrayInputStream("test utf8 data".toByteArray())
    val res = useCase.run(UploadFileInputData(UriInfo(dummyUri), inputStream))
    Assert.assertThat(res.postOEmbedRaw.type, `is`(OEmbed.type))
    Assert.assertThat(res.postOEmbedRaw.value.replacementFileValue.fileId, `is`(file.id))
    Assert.assertThat(res.postOEmbedRaw.value.replacementFileValue.fileToken, `is`(file.fileToken))
  }
}