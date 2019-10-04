package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.Relationship

data class UpdateRelationshipInputData(
    val userId: String,
    val relationship: Relationship
)
