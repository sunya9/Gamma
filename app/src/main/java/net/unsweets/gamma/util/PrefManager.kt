package net.unsweets.gamma.util

import android.content.Context

class PrefManager(val context: Context) {
    val name = "Store"
    enum class PrefKey(val key: String) {
        ID("id"),
        TOKEN("token")
    }
    private val sharedPref
        get() = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val editor
        get() = sharedPref.edit()
    fun setDefaultAccount(id: String, token: String) {
        val myEditor = editor
        myEditor.putString(PrefKey.ID.key, id)
        myEditor.putString(PrefKey.TOKEN.key, token)
        myEditor.commit()
    }

    fun getDefaultAccountID(): String? = sharedPref.getString(PrefKey.ID.key, null)

    fun getDefaultAccountToken(): String? {
        return sharedPref.getString(PrefKey.TOKEN.key, null)
    }
}