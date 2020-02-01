package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.io.RepostInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Posts
import net.unsweets.gamma.util.Response
import net.unsweets.gamma.util.TestException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsNull
import org.junit.Assert
import org.junit.Test


class RepostUseCaseTest {
    private val post = Posts.normalPost.copy(id = "1")

    @Test
    fun succeedToRepost() {
        val pnutRepositoryMock = object : PnutRepositoryMock() {
            override fun createRepostSync(postId: String): PnutResponse<Post> {
                val hasRepostOfPost = Posts.normalPost.copy(repostOf = post, youReposted = true)
                return Response.success(hasRepostOfPost)
            }
        }
        val repostUseCase = RepostUseCase(pnutRepositoryMock)
        val res = repostUseCase.run(RepostInputData("1", true))
        Assert.assertThat(res.res.data.youReposted, `is`(true))
        Assert.assertThat(res.res.data.repostOf?.id, `is`("1"))
    }

    @Test(expected = TestException::class)
    fun failToRepost() {
        val pnutRepositoryMock = object : PnutRepositoryMock() {
            override fun createRepostSync(postId: String): PnutResponse<Post> {
                throw TestException()
            }
        }
        val repostUseCase = RepostUseCase(pnutRepositoryMock)
        repostUseCase.run(RepostInputData("1", true))
    }


    @Test
    fun succeedToDeleteRepost() {
        val pnutRepositoryMock = object : PnutRepositoryMock() {
            override fun deleteRepostSync(postId: String): PnutResponse<Post> {
                return Response.success(Posts.normalPost)
            }
        }
        val repostUseCase = RepostUseCase(pnutRepositoryMock)
        val res = repostUseCase.run(RepostInputData("1", false))
        Assert.assertThat(res.res.data.youReposted, `is`(false))
        Assert.assertThat(res.res.data.repostOf, IsNull<Post>())
    }

    @Test(expected = TestException::class)
    fun failToDeleteRepost() {
        val pnutRepositoryMock = object : PnutRepositoryMock() {
            override fun deleteRepostSync(postId: String): PnutResponse<Post> {
                throw TestException()
            }
        }
        val repostUseCase = RepostUseCase(pnutRepositoryMock)
        repostUseCase.run(RepostInputData("1", false))
    }
}
