package net.unsweets.gamma.domain.repository

import net.unsweets.gamma.domain.model.Account

interface IAccountRepository {
    fun getToken(id: String): String?
    fun addAccount(id: String, screenName: String, name: String, token: String)
    fun deleteAccount(id: String)
    fun getStoredIds(): List<String>
    fun setAccountIds(ids: List<String>)
    fun getAccount(id: String): Account?
    fun updateDefaultAccount(id: String)
    fun getDefaultAccount(): Account?
}
