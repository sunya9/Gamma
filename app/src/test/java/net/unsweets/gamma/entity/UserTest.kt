package net.unsweets.gamma.entity

import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.sample.Users
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Test

class UserTest {
  @Test
  fun itIsMe() {
    val me = Users.me
    Assert.assertThat(me.me, `is`(true))
  }

  @Test
  fun itIsNotMe() {
    val me = Users.others
    Assert.assertThat(me.me, `is`(false))
  }

  @Test
  fun getCanonicalUrl() {
    val canonicalUrl = User.getCanonicalUrl("abc")
    Assert.assertThat(canonicalUrl, `is`("https://pnut.io/@abc"))
  }

  @Test
  fun getCoverUrl() {
    val coverUrl = User.getCoverUrl("123")
    Assert.assertThat(coverUrl, `is`("https://api.pnut.io/v0/users/123/cover"))
  }

  @Test
  fun avatarSize() {
    val defaultSizeAvatarUrl = User.getAvatarUrl("123")
    Assert.assertThat(defaultSizeAvatarUrl, `is`("https://api.pnut.io/v0/users/123/avatar?h=64"))

    val originalAvatarUrl = User.getAvatarUrl("123", null)
    Assert.assertThat(originalAvatarUrl, `is`("https://api.pnut.io/v0/users/123/avatar"))

    val size24AvatarUrl = User.getAvatarUrl("123", User.AvatarSize.Mini)
    Assert.assertThat(size24AvatarUrl, `is`("https://api.pnut.io/v0/users/123/avatar?h=24"))
    val size48AvatarUrl = User.getAvatarUrl("123", User.AvatarSize.Small)
    Assert.assertThat(size48AvatarUrl, `is`("https://api.pnut.io/v0/users/123/avatar?h=48"))

    val size64AvatarUrl = User.getAvatarUrl("123", User.AvatarSize.Normal)
    Assert.assertThat(size64AvatarUrl, `is`("https://api.pnut.io/v0/users/123/avatar?h=64"))

    val size96AvatarUrl = User.getAvatarUrl("123", User.AvatarSize.Large)
    Assert.assertThat(size96AvatarUrl, `is`("https://api.pnut.io/v0/users/123/avatar?h=96"))

    val user = Users.me
    val link = user.content.avatarImage.link
    val originalAvatarUrlOfUser = User.getAvatarUrl(user, null)
    Assert.assertThat(originalAvatarUrlOfUser, `is`(link))

    val size24AvatarUrlOfUser = User.getAvatarUrl(user, User.AvatarSize.Mini)
    Assert.assertThat(size24AvatarUrlOfUser, `is`("$link?h=24"))

    val size48AvatarUrlOfUser = User.getAvatarUrl(user, User.AvatarSize.Small)
    Assert.assertThat(size48AvatarUrlOfUser, `is`("$link?h=48"))

    val size64AvatarUrlOfUser = User.getAvatarUrl(user, User.AvatarSize.Normal)
    Assert.assertThat(size64AvatarUrlOfUser, `is`("$link?h=64"))

    val size96AvatarUrlOfUser = User.getAvatarUrl(user, User.AvatarSize.Large)
    Assert.assertThat(size96AvatarUrlOfUser, `is`("$link?h=96"))

  }
}