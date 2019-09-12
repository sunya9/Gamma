package net.unsweets.gamma.domain.repository

import net.unsweets.gamma.presentation.util.ThemeColorUtil

interface IPreferenceRepository {
    fun load()
    fun reload()
    val themeColor: ThemeColorUtil.ThemeColor
    val darkMode: ThemeColorUtil.DarkMode
    val darkModeStr: String
}