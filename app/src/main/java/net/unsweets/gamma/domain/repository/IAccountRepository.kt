package net.unsweets.gamma.domain.repository

import net.unsweets.gamma.data.db.entities.Account

interface IAccountRepository {
    fun getToken(id: String): String?
    fun addAccount(id: String, token: String)
    fun deleteAccount(account: Account)
    fun getStoredIds(): List<String>
}
