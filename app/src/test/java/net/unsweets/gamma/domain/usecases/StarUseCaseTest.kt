package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.io.StarInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.sample.Posts
import net.unsweets.gamma.util.TestException
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test


class StarUseCaseTest {
    private val unStarredPost = Posts.unStarredPost
    private val starredPost = Posts.starredPost

    @Test
    fun succeedToStar() {
        val starUseCase = StarUseCase(object : PnutRepositoryMock() {
            override fun createStarPostSync(postId: String): PnutResponse<Post> {
                return PnutResponse(
                    PnutResponse.Meta(200),
                    unStarredPost.copy(youBookmarked = true)
                )
            }
        })
        val starOutputData = starUseCase.run(StarInputData(unStarredPost.id, true))
        Assert.assertThat(starOutputData.res.data, `is`(unStarredPost.copy(youBookmarked = true)))
    }

    @Test(expected = TestException::class)
    fun failToStar() {
        val starUseCase = StarUseCase(object : PnutRepositoryMock() {
            override fun createStarPostSync(postId: String): PnutResponse<Post> {
                throw TestException()
            }
        })
        starUseCase.run(StarInputData(starredPost.id, true))
    }

    @Test
    fun succeedToUnStar() {
        val starUseCase = StarUseCase(object : PnutRepositoryMock() {
            override fun deleteStarPostSync(postId: String): PnutResponse<Post> {
                return PnutResponse(PnutResponse.Meta(200), starredPost.copy(youBookmarked = false))
            }
        })
        val starOutputData = starUseCase.run(StarInputData(starredPost.id, false))
        Assert.assertThat(starOutputData.res.data, `is`(starredPost.copy(youBookmarked = false)))
    }

    @Test(expected = TestException::class)
    fun failToUnStar() {
        val starUseCase = StarUseCase(object : PnutRepositoryMock() {
            override fun deleteStarPostSync(postId: String): PnutResponse<Post> {
                throw TestException()
            }
        })
        starUseCase.run(StarInputData(unStarredPost.id, false))
    }
}