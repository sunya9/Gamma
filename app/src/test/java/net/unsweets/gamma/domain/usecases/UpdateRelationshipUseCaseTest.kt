package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.runBlocking
import net.unsweets.gamma.domain.Relationship
import net.unsweets.gamma.domain.model.io.UpdateRelationshipInputData
import net.unsweets.gamma.mock.Mocks
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class UpdateRelationshipUseCaseTest {
    private val followUseCase = UpdateRelationshipUseCase(Mocks.pnutRepository)
    @Test
    fun succeedToFollow() {
        val input = UpdateRelationshipInputData("2", Relationship.Follow)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(true))
    }

    @Test(expected = Exception::class)
    fun failToFollow() {
        val input = UpdateRelationshipInputData("3", Relationship.Follow)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToUnFollow() {
        val input = UpdateRelationshipInputData("3", Relationship.UnFollow)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(false))
    }

    @Test(expected = Exception::class)
    fun failToUnFollow() {
        val input = UpdateRelationshipInputData("2", Relationship.UnFollow)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToBlock() {
        val input = UpdateRelationshipInputData("3", Relationship.Block)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youBlocked, `is`(false))
    }

    @Test(expected = Exception::class)
    fun failToBlock() {
        val input = UpdateRelationshipInputData("4", Relationship.Block)
        runBlocking { followUseCase.run(input) }
    }

    @Test
    fun succeedToUnBlock() {
        val input = UpdateRelationshipInputData("4", Relationship.UnBlock)
        val output = runBlocking { followUseCase.run(input) }
        Assert.assertThat(output.res.data.youFollow, `is`(false))
    }

    @Test(expected = Exception::class)
    fun failToUnBlock() {
        val input = UpdateRelationshipInputData("3", Relationship.UnBlock)
        runBlocking { followUseCase.run(input) }
    }
}