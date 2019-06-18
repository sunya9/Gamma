package net.unsweets.gamma.domain.model

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post

data class PostOutputData(
    val res: PnutResponse<Post>
)
