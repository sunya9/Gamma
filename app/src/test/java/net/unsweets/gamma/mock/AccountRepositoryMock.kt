package net.unsweets.gamma.mock

import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.repository.IAccountRepository

class AccountRepositoryMock : IAccountRepository {
    private val accounts = hashMapOf(
        "1" to Account("1", "validToken", "foo", "bar")
    )
    private var defaultAccount: Account? = null

    override fun getToken(id: String): String? {
        return accounts[id]?.token
    }

    override fun addAccount(id: String, screenName: String, name: String, token: String) {
        accounts[id] = Account(id, token, screenName, name)
    }

    override fun deleteAccount(id: String) {
        accounts.remove(id)
    }

    override fun getStoredIds(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAccountIds(ids: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccount(id: String): Account? {
        return accounts[id]
    }

    override fun updateDefaultAccount(id: String) {
        defaultAccount = accounts[id]
    }

    override fun getDefaultAccount(): Account? {
        return defaultAccount
    }
}
