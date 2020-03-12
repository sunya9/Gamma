package net.unsweets.gamma.sample

import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.entities.Entities
import net.unsweets.gamma.util.RandomID
import java.util.*

object Posts {
    val normalPost
        get() = Post(
            id = RandomID.get,
            createdAt = Date(),
            youBookmarked = false,
          youReposted = false,
          content = Post.PostContent(
            text = "post",
            html = "<span>post</span>",
            entities = Entities(emptyList(), emptyList(), emptyList()),
            linksNotParsed = false
          )
        )
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