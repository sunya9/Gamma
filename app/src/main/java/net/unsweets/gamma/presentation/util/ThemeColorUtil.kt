package net.unsweets.gamma.presentation.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.repository.PreferenceRepository

object ThemeColorUtil {
    @Suppress("DEPRECATION")
    enum class DarkMode(val value: Int) {
        FollowSystem(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        Off(AppCompatDelegate.MODE_NIGHT_NO),
        On(AppCompatDelegate.MODE_NIGHT_YES),
        Auto(AppCompatDelegate.MODE_NIGHT_AUTO)
    }

    enum class ThemeColor(val themeResource: Int) {
        Default(R.style.AdditionalStyle_Default),
        Red(R.style.AdditionalStyle_Red),
        Pink(R.style.AdditionalStyle_Pink),
        Purple(R.style.AdditionalStyle_Purple),
        DeepPurple(R.style.AdditionalStyle_DeepPurple),
        Indigo(R.style.AdditionalStyle_Indigo),
        Blue(R.style.AdditionalStyle_Blue),
        LightBlue(R.style.AdditionalStyle_LightBlue),
        Cyan(R.style.AdditionalStyle_Cyan),
        Teal(R.style.AdditionalStyle_Teal),
        Green(R.style.AdditionalStyle_Green),
        LightGreen(R.style.AdditionalStyle_LightGreen),
        Lime(R.style.AdditionalStyle_Lime),
        Amber(R.style.AdditionalStyle_Amber),
        Orange(R.style.AdditionalStyle_Orange),
        DeepOrange(R.style.AdditionalStyle_DeepOrange),
        Brown(R.style.AdditionalStyle_Brown),
        Grey(R.style.AdditionalStyle_Grey),
        BlueGrey(R.style.AdditionalStyle_BlueGrey);

        @ColorInt
        fun getColor(context: Context): Int {
            val theme = context.resources.newTheme().apply {
                applyStyle(themeResource, true)
            }
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            return typedValue.data
        }

        companion object {
            fun fromString(text: String?): ThemeColor {
                if (text == null) return Default
                return try {
                    valueOf(text)
                } catch (e: Exception) {
                    Default
                }
            }
        }
    }

    // TODO: Fix me
    fun currentDarkThemeMode(context: Context?): String {
        if (context == null) return "0"
        val preference = PreferenceRepository(context)
        return preference.darkModeStr
    }

    // TODO: Fix me
    fun getThemeColor(context: Context?): ThemeColor {
        if (context == null) return ThemeColor.Default
        val preference = PreferenceRepository(context)
        return preference.themeColor
    }

    fun applyTheme(context: Context): ThemeColor {
        return getThemeColor(context).let {
            context.theme.applyStyle(it.themeResource, true)
            it
        }
    }


    fun applyTheme(fragment: DialogFragment) {
        val context = fragment.context ?: return
        getThemeColor(context).let {
            fragment.setStyle(DialogFragment.STYLE_NORMAL, it.themeResource)
        }
    }
}