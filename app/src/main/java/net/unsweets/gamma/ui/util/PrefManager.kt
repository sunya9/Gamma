package net.unsweets.gamma.ui.util

import android.content.Context

class PrefManager(val context: Context) {
    val name = "Store"
    enum class PrefKey(val key: String) {
        ID("id"),
        Token("token")
    }
    private val sharedPref
        get() = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor
        get() = sharedPref.edit()
    fun setDefaultAccount(id: String, token: String) {
        val myEditor = editor
        myEditor.putString(PrefKey.ID.key, id)
        myEditor.putString(PrefKey.Token.key, token)
        myEditor.commit()
    }

    fun getDefaultAccountID(): String? = sharedPref.getString(PrefKey.ID.key, null)

    fun getDefaultAccountToken(): String? {
        return sharedPref.getString(PrefKey.Token.key, null)
    }

    fun removeDefaultAccountIDAndToken(): Boolean {
        val myEditor = editor
        myEditor.remove(PrefKey.ID.key)
        myEditor.remove(PrefKey.Token.key)
        return myEditor.commit()
    }
}