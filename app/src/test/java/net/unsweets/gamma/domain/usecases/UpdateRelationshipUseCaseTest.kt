package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.Relationship
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.UpdateRelationshipInputData
import net.unsweets.gamma.mock.PnutRepositoryMock
import net.unsweets.gamma.util.TestException
import net.unsweets.sample.Users
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class UpdateRelationshipUseCaseTest {
    private fun getFollowUseCase(vararg users: User): UpdateRelationshipUseCase {
        val mockData = PnutRepositoryMock.PnutMockData(users = listOf(*users))
        val db = PnutRepositoryMock(mockData)
        return UpdateRelationshipUseCase(db)
    }

    @Test
    fun succeedToFollow() {
        val others = Users.others
        val followUseCase = getFollowUseCase(others)
        val input = UpdateRelationshipInputData(others.id, Relationship.Follow)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(true))
    }

    @Test(expected = TestException::class)
    fun failToFollow() {
        val alreadyFollowingUser = Users.others.copy(youFollow = true)
        val followUseCase = getFollowUseCase(alreadyFollowingUser)
        val input = UpdateRelationshipInputData(alreadyFollowingUser.id, Relationship.Follow)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToUnFollow() {
        val alreadyFollowingUser = Users.others.copy(youFollow = true)
        val followUseCase = getFollowUseCase(alreadyFollowingUser)
        val input = UpdateRelationshipInputData(alreadyFollowingUser.id, Relationship.UnFollow)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(false))
    }

    @Test(expected = TestException::class)
    fun failToUnFollow() {
        val alreadyFollowingUser = Users.others
        val followUseCase = getFollowUseCase(alreadyFollowingUser)
        val input = UpdateRelationshipInputData(alreadyFollowingUser.id, Relationship.UnFollow)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToBlock() {
        val others = Users.others
        val followUseCase = getFollowUseCase(others)
        val input = UpdateRelationshipInputData(others.id, Relationship.Block)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youBlocked, `is`(true))
        Assert.assertThat(output.res.data.youFollow, `is`(false))
    }

    @Test(expected = TestException::class)
    fun failToBlock() {
        val alreadyBlockedUser = Users.others.copy(youBlocked = true)
        val followUseCase = getFollowUseCase(alreadyBlockedUser)
        val input = UpdateRelationshipInputData(alreadyBlockedUser.id, Relationship.Block)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToUnBlock() {
        val alreadyBlockedUser = Users.others.copy(youBlocked = true)
        val followUseCase = getFollowUseCase(alreadyBlockedUser)
        val input = UpdateRelationshipInputData(alreadyBlockedUser.id, Relationship.UnBlock)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(false))
    }

    @Test(expected = TestException::class)
    fun failToUnBlock() {
        val others = Users.others
        val followUseCase = getFollowUseCase(others)
        val input = UpdateRelationshipInputData(others.id, Relationship.UnBlock)
        runBlocking { followUseCase.run(input) }
    }
}