package net.unsweets.gamma.domain.repository

interface IPreferenceRepository {
    fun getToken(id: String): String?
    fun setDefaultAccount(id: String, token: String)
    fun getDefaultAccountID(): String?
    fun getDefaultAccountToken(): String?
    fun removeDefaultAccountIDAndToken(): Boolean
}