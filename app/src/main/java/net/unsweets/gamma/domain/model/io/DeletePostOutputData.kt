package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post

data class DeletePostOutputData(
    val res: PnutResponse<Post>
)
