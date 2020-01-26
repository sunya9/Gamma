package net.unsweets.sample

import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.util.RandomID
import java.util.*

object Posts {
    val unStarredPost
        get() = Post(
            id = RandomID.get,
            createdAt = Date(),
            youBookmarked = false
        )

    val starredPost
        get() = Post(
            id = RandomID.get,
            createdAt = Date(),
            youBookmarked = true
        )
}