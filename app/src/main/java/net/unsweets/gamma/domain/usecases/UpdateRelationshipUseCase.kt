package net.unsweets.gamma.domain.usecases

import net.unsweets.gamma.domain.Relationship
import net.unsweets.gamma.domain.model.io.UpdateRelationshipInputData
import net.unsweets.gamma.domain.model.io.UpdateRelationshipOutputData
import net.unsweets.gamma.domain.repository.IPnutRepository

class UpdateRelationshipUseCase(val pnutRepository: IPnutRepository) :
    AsyncUseCase<UpdateRelationshipOutputData, UpdateRelationshipInputData>() {
    override suspend fun run(params: UpdateRelationshipInputData): UpdateRelationshipOutputData {
        val userId = params.userId
        val res = when (params.relationship) {
            Relationship.Follow -> pnutRepository.follow(userId)
            Relationship.UnFollow -> pnutRepository.unFollow(userId)
            Relationship.Block -> pnutRepository.block(userId)
            Relationship.UnBlock -> pnutRepository.unBlock(userId)
        }
        return UpdateRelationshipOutputData(res)
    }
}