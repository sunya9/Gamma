package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.PageableItemWrapper
import net.unsweets.gamma.domain.model.StreamType

data class CachePostInputData(
    val list: List<PageableItemWrapper<Post>>,
    val streamType: StreamType
)
