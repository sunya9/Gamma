package net.unsweets.gamma.domain.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.ThemeColorUtil

class PreferenceRepository(val context: Context) : IPreferenceRepository {
    val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
    private val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()

    init {
        load()
    }

    override fun reload() = load()

    override fun load() {
    }

    override val themeColor: ThemeColorUtil.ThemeColor
        get() {
            val themeColorStr =
                sharedPreferences.getString(
                    context.getString(R.string.pref_change_primary_color_key),
                    null
                )
            return ThemeColorUtil.ThemeColor.fromString(themeColorStr)
        }

    override val darkMode: ThemeColorUtil.DarkMode
        get() {
            return try {
                val strInt = sharedPreferences.getString(
                    context.getString(R.string.pref_dark_theme_key),
                    "0"
                ) ?: "0"
                ThemeColorUtil.DarkMode.values()[strInt.toInt()]
            } catch (e: Exception) {
                ThemeColorUtil.DarkMode.FollowSystem
            }
        }

    override fun onRegisterChangePreference(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onUnregisterChangePreference(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override val darkModeStr: String
        get() {
            return sharedPreferences.getString(context.getString(R.string.pref_dark_theme_key), "0")
                ?: "0"
        }

    override val avatarSwipe: Boolean
        get() = sharedPreferences.getBoolean(context.getString(R.string.avatar_swipe_key), true)

    override val loadingSize: Int
        get() = sharedPreferences.getInt(context.getString(R.string.pref_loading_size_key), 20)
}