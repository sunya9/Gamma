package net.unsweets.gamma.sample

import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.entity.image.Avatar
import net.unsweets.gamma.domain.entity.image.Cover
import net.unsweets.gamma.util.RandomID
import java.util.*

object Users {
    val me
        get() = User(
            id = RandomID.get,
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
          youFollow = true,
        youMuted = false,
        locale = "ja_JP",
        username = "foo",
        name = "bar",
        type = User.AccountType.HUMAN,
        timezone = "Asia/Tokyo"
    )

    val others
        get() = User(
            id = RandomID.get,
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

    val user3 = User(
        id = "3",
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

    val user4 = User(
        id = "4",
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
        youBlocked = true,
        youCanFollow = true,
        youFollow = true,
        youMuted = false,
        locale = "ja_JP",
        username = "foo",
        name = "bar",
        type = User.AccountType.HUMAN,
        timezone = "Asia/Tokyo"
    )

    val user5 = User(
        id = "5",
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
        youMuted = true,
        locale = "ja_JP",
        username = "foo",
        name = "bar",
        type = User.AccountType.HUMAN,
        timezone = "Asia/Tokyo"
    )
}