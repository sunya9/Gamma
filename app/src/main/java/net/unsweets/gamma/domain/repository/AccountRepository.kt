package net.unsweets.gamma.domain.repository

import android.content.Context
import net.unsweets.gamma.domain.model.Account


class AccountRepository(context: Context) : AbstractPreferenceRepository(context), IAccountRepository {
    override val name = "Account"

    private enum class Key { AccountList, DefaultAccount }
    private enum class AccountKey {
        ID, Token, Name, ScreenName;

        fun getKey(id: String): String = "$id-${this.name}"
    }

    override fun updateDefaultAccount(id: String) {
        editor.putString(Key.DefaultAccount.name, id).commit()
    }

    override fun getDefaultAccount(): Account? {
        val id = sharedPreferences.getString(Key.DefaultAccount.name, null) ?: return null
        return getAccount(id)
    }

    override fun getAccount(id: String): Account? {
        return Account(
            sharedPreferences.getString(AccountKey.ID.getKey(id), null) ?: return null,
            sharedPreferences.getString(AccountKey.Token.getKey(id), null) ?: return null,
            sharedPreferences.getString(AccountKey.ScreenName.getKey(id), null) ?: return null,
            sharedPreferences.getString(AccountKey.Name.getKey(id), null) ?: return null
        )
    }

    private val delimiter = ","
    override fun setAccountIds(ids: List<String>) {
        editor.putString(Key.AccountList.name, ids.joinToString(delimiter)).commit()
    }

    override fun getStoredIds(): List<String> {
        val refined = (sharedPreferences.getString(Key.AccountList.name, "") ?: "").split(delimiter).toSet().toList()
        setAccountIds(refined)
        return refined
    }
    override fun getToken(id: String): String? {
        return sharedPreferences.getString(AccountKey.Token.getKey(id), null)
    }

    override fun addAccount(id: String, screenName: String, name: String, token: String) {
        editor.putString(AccountKey.ID.getKey(id), id)
            .putString(AccountKey.ScreenName.getKey(id), screenName)
            .putString(AccountKey.Name.getKey(id), name)
            .putString(AccountKey.Token.getKey(id), token)
            .commit()
        val newList = getStoredIds().toMutableList().apply {
            if (indexOf(id) >= 0) return@apply
            add(id)
        }.toList()
        setAccountIds(newList)
    }

    override fun deleteAccount(account: Account) {
        val id = account.id
        editor.remove(AccountKey.ID.getKey(id))
            .remove(AccountKey.Token.getKey(id))
            .remove(AccountKey.ScreenName.getKey(id))
            .remove(AccountKey.Name.getKey(id))
            .commit()
        val newList = getStoredIds().toMutableList().apply {
            remove(id)
        }.toList()
        setAccountIds(newList)
    }

}