package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.model.StreamType
import net.unsweets.gamma.domain.model.params.composed.GetPostsParam

class GetPostInputData(
    val streamType: StreamType,
    val params: GetPostsParam
)
