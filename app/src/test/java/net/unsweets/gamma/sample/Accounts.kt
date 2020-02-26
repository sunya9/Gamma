package net.unsweets.gamma.sample

import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.util.RandomID

object Accounts {
  val account
    get() = Account(RandomID.get, "token", "screenName", "name")

}