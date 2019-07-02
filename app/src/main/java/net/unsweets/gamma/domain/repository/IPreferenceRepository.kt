package net.unsweets.gamma.domain.repository

interface IPreferenceRepository {
    fun getDefaultAccountID(): String?
    fun updateDefaultAccountID(id: String): Boolean
    fun removeDefaultAccountIDAndToken(): Boolean
}