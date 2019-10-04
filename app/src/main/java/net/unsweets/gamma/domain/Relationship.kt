package net.unsweets.gamma.domain

import net.unsweets.gamma.domain.entity.User

enum class Relationship {
    Follow, UnFollow,
    Block, UnBlock;

    companion object {
        fun getRelationship(user: User): Relationship {
            return when {
                user.youFollow -> Follow
                !user.youFollow -> UnFollow
                user.youBlocked -> Block
                else -> UnFollow
            }
        }
    }
}