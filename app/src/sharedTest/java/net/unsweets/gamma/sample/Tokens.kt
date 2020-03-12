package net.unsweets.gamma.sample

import net.unsweets.gamma.domain.entity.Token

object Tokens {
  val token
    get() = Token(Clients.testClient, emptyList(), Users.me, Token.Storage(0, 0))
}