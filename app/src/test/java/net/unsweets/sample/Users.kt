package net.unsweets.sample

import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.entity.image.Avatar
import net.unsweets.gamma.domain.entity.image.Cover
import java.util.*

object Users {
    fun getUser(id: String): User {
        return db[id] ?: error("")
    }

    private val user1 = User(
        id = "1",
        content = User.UserContent(
            avatarImage = Avatar(false, 1, 1, "https://example.com/avatar"),
            coverImage = Cover(false, 1, 1, "https://example.com/avatar"),
            entities = null,
            text = null,
            markdownText = null,
            html = null
        ),
        badge = null,
        counts = User.UserCount(0, 0, 0, 0, 0, 0),
        createdAt = Date(),
        followsYou = true,
        youBlocked = false,
        youCanFollow = false,
        youFollow = false,
        youMuted = false,
        locale = "ja_JP",
        username = "foo",
        name = "bar",
        type = User.AccountType.HUMAN,
        timezone = "Asia/Tokyo"
    )

    private val user2 = User(
        id = "2",
        content = User.UserContent(
            avatarImage = Avatar(false, 1, 1, "https://example.com/avatar"),
            coverImage = Cover(false, 1, 1, "https://example.com/avatar"),
            entities = null,
            text = null,
            markdownText = null,
            html = null
        ),
        badge = null,
        counts = User.UserCount(0, 0, 0, 0, 0, 0),
        createdAt = Date(),
        followsYou = true,
        youBlocked = false,
        youCanFollow = true,
        youFollow = false,
        youMuted = false,
        locale = "ja_JP",
        username = "foo",
        name = "bar",
        type = User.AccountType.HUMAN,
        timezone = "Asia/Tokyo"
    )

    private val user3 = User(
        id = "2",
        content = User.UserContent(
            avatarImage = Avatar(false, 1, 1, "https://example.com/avatar"),
            coverImage = Cover(false, 1, 1, "https://example.com/avatar"),
            entities = null,
            text = null,
            markdownText = null,
            html = null
        ),
        badge = null,
        counts = User.UserCount(0, 0, 0, 0, 0, 0),
        createdAt = Date(),
        followsYou = true,
        youBlocked = false,
        youCanFollow = true,
        youFollow = true,
        youMuted = false,
        locale = "ja_JP",
        username = "foo",
        name = "bar",
        type = User.AccountType.HUMAN,
        timezone = "Asia/Tokyo"
    )
    private val db = mapOf(
        "1" to user1,
        "2" to user2,
        "3" to user3
    )

}