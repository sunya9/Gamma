package net.unsweets.gamma.domain.model.io

import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.model.CachedList

data class GetCachedPostListOutputData(
    val posts: CachedList<Post>
)
