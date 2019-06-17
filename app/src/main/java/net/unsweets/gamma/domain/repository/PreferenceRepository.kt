package net.unsweets.gamma.domain.repository

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

class PreferenceRepository(val context: Context): IPreferenceRepository {
    private val sharedPref: SharedPreferences by lazy { context.getSharedPreferences(name, Context.MODE_PRIVATE) }
    private val name = "Store"
    enum class PrefKey(val key: String) {
        ID("id"),
        Token("token")
    }
    private val editor
        get() = sharedPref.edit()

    override fun getToken(id: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    override fun setDefaultAccount(id: String, token: String) {
        val myEditor = editor
        myEditor.putString(PrefKey.ID.key, id)
        myEditor.putString(PrefKey.Token.key, token)
        myEditor.commit()
    }

    override fun getDefaultAccountID(): String? = sharedPref.getString(PrefKey.ID.key, null)

    override fun getDefaultAccountToken(): String? {
        return sharedPref.getString(PrefKey.Token.key, null)
    }

    override fun removeDefaultAccountIDAndToken(): Boolean {
        val myEditor = editor
        myEditor.remove(PrefKey.ID.key)
        myEditor.remove(PrefKey.Token.key)
        return myEditor.commit()
    }
}