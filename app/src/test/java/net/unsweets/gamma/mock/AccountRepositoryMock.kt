package net.unsweets.gamma.mock

import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.repository.IAccountRepository

class AccountRepositoryMock : IAccountRepository {
    private val accounts = hashMapOf(
        "1" to Account("1", "validTOken", "foo", "bar")
    )

    override fun getToken(id: String): String? {
        return accounts[id]?.token
    }

    override fun addAccount(id: String, screenName: String, name: String, token: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAccount(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStoredIds(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAccountIds(ids: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccount(id: String): Account? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDefaultAccount(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDefaultAccount(): Account? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
