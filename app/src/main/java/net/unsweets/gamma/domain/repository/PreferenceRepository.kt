package net.unsweets.gamma.domain.repository

import android.content.Context

class PreferenceRepository(val context: Context) : AbstractPreferenceRepository(context), IPreferenceRepository {
    override val name = "Store"
    enum class PrefKey(val key: String) {
        ID("id"),
        Token("token")
    }

    override fun getDefaultAccountID(): String? = sharedPreferences.getString(PrefKey.ID.key, null)

    override fun updateDefaultAccountID(id: String): Boolean {
        return editor.putString(PrefKey.ID.name, id).commit()
    }

    override fun removeDefaultAccountIDAndToken(): Boolean {
        return editor.run {
            remove(PrefKey.ID.key)
            remove(PrefKey.Token.key)
            commit()
        }
    }
}