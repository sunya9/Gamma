package net.unsweets.gamma.domain.repository

import android.content.Context
import net.unsweets.gamma.data.db.entities.Account


class AccountRepository(context: Context) : AbstractPreferenceRepository(context), IAccountRepository {
    override fun getStoredIds(): List<String> {
        return sharedPreferences.all.keys.toList()
    }

    override val name = "Account"

    override fun getToken(id: String): String? {
        return sharedPreferences.getString(id, null)
    }

    override fun addAccount(id: String, token: String) {
        editor.putString(id, token).commit()
    }

    override fun deleteAccount(account: Account) {
        editor.remove(account.id).commit()
    }

}