package net.unsweets.gamma.mock

import net.unsweets.gamma.domain.model.Account
import net.unsweets.gamma.domain.repository.IAccountRepository

open class AccountRepositoryMock(private val accounts: List<Account> = emptyList()) :
    IAccountRepository {
    private val memoryDb by lazy {
        accounts.map { it.id to it }.toMap().toMutableMap()
    }
    private var defaultAccount: Account? = null

    override fun getToken(id: String): String? {
        return memoryDb[id]?.token
    }

    override fun addAccount(id: String, screenName: String, name: String, token: String) {
        memoryDb[id] = Account(id, token, screenName, name)
    }

    override fun deleteAccount(id: String) {
        memoryDb.remove(id)
    }

    override fun getStoredIds(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAccountIds(ids: List<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccount(id: String): Account? {
        return memoryDb[id]
    }

    override fun updateDefaultAccount(id: String) {
        defaultAccount = memoryDb[id]
    }

    override fun getDefaultAccount(): Account? {
        return defaultAccount
    }
}
