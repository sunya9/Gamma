package net.unsweets.gamma.domain.repository

import android.content.SharedPreferences
import net.unsweets.gamma.presentation.util.ThemeColorUtil

interface IPreferenceRepository {
    fun onRegisterChangePreference(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun onUnregisterChangePreference(listener: SharedPreferences.OnSharedPreferenceChangeListener)
    fun load()
    fun reload()
    val themeColor: ThemeColorUtil.ThemeColor
    val darkMode: ThemeColorUtil.DarkMode
    val darkModeStr: String
    val avatarSwipe: Boolean
    val loadingSize: Int
}