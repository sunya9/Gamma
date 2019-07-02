package net.unsweets.gamma.domain.repository

import android.content.Context
import android.content.SharedPreferences

abstract class AbstractPreferenceRepository(context: Context) {
    abstract val name: String
    protected val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            name,
            Context.MODE_PRIVATE
        )
    }
    protected val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()
}