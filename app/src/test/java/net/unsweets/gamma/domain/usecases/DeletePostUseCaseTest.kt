package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.io.DeletePostInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Posts
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert
import org.junit.Test

class DeletePostUseCaseTest {
  @Test
  fun success() {
    val post = Posts.normalPost
    val useCase = DeletePostUseCase(object : PnutRepositoryMock() {
      override fun deletePost(postId: String): PnutResponse<Post> {
        return PnutResponse(
          PnutResponse.Meta(200), post.copy(
            isDeleted = true, content = null
          )
        )
      }
    })
    val res = runBlocking {
      useCase.run(DeletePostInputData(post.id))
    }
    Assert.assertThat(res.res.data.isDeleted, `is`(true))
    Assert.assertThat(res.res.data.content, `is`(nullValue()))
  }
}