package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.io.GetPostInputData
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.mock.PreferenceRepositoryMock
import net.unsweets.gamma.sample.Posts
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class GetPostUseCaseTest {

  private fun generatePosts(prefix: String): List<Post> {
    val post1 = Posts.normalPost.copy(content = Posts.normalPost.content?.copy(text = "${prefix}1"))
    val post2 = Posts.normalPost.copy(content = Posts.normalPost.content?.copy(text = "${prefix}2"))
    val post3 = Posts.normalPost.copy(content = Posts.normalPost.content?.copy(text = "${prefix}3"))
    return listOf(post1, post2, post3)
  }

  @Test
  fun getPersonalStream() {

    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getPersonalStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("home")
        )
      }
    }, PreferenceRepositoryMock())
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.Home, GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("home1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("home2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("home3"))
  }

  @Test
  fun getUnifiedStream() {
    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getUnifiedStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("unified")
        )
      }
    }, object : PreferenceRepositoryMock() {
      override val unifiedStream: Boolean = true
    })
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.Home, GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("unified1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("unified2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("unified3"))
  }

  @Test
  fun getMentionStream() {
    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getMentionStream(getPostsParam: GetPostsParam): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("mention")
        )
      }
    }, PreferenceRepositoryMock())
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.Mentions, GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("mention1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("mention2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("mention3"))
  }

  @Test
  fun getStarsPosts() {
    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getStars(
        userId: String,
        getPostsParam: GetPostsParam
      ): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("stars")
        )
      }
    }, PreferenceRepositoryMock())
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.Stars("me"), GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("stars1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("stars2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("stars3"))
  }

  @Test
  fun getTagPosts() {
    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getTagStream(
        tag: String,
        getPostsParam: GetPostsParam
      ): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("tag")
        )
      }
    }, PreferenceRepositoryMock())
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.Tag("tag"), GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("tag1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("tag2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("tag3"))
  }


  @Test
  fun getUserPosts() {
    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getUserPosts(
        userId: String,
        getPostsParam: GetPostsParam
      ): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("user")
        )
      }
    }, PreferenceRepositoryMock())
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.User("me"), GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("user1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("user2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("user3"))
  }

  @Test
  fun getThreadPosts() {
    val useCase = GetPostUseCase(object : PnutRepositoryMock() {
      override suspend fun getThread(
        postId: String,
        params: GetPostsParam
      ): PnutResponse<List<Post>> {
        return PnutResponse(
          PnutResponse.Meta(200), generatePosts("thread")
        )
      }
    }, PreferenceRepositoryMock())
    val res = runBlocking { useCase.run(GetPostInputData(StreamType.Thread("1"), GetPostsParam())) }
    Assert.assertThat(res.res.data[0].content?.text, `is`("thread1"))
    Assert.assertThat(res.res.data[1].content?.text, `is`("thread2"))
    Assert.assertThat(res.res.data[2].content?.text, `is`("thread3"))
  }
}