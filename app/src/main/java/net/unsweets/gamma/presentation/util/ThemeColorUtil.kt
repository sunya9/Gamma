package net.unsweets.gamma.presentation.util

import android.content.Context
import android.preference.PreferenceManager
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import net.unsweets.gamma.R

object ThemeColorUtil {
    enum class ThemeColor(val themeResource: Int) {
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
            fun fromString(text: String?): ThemeColor? {
                if (text == null) return null
                return try {
                    valueOf(text)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    fun currentDarkThemeMode(context: Context?): String {
        if (context == null) return "0"
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        return pref.getString(context.getString(R.string.pref_dark_theme_key), "0") ?: "0"
    }

    fun getThemeColor(context: Context?): ThemeColor? {
        if (context == null) return null
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val themeColorStr =
            pref.getString(context.getString(R.string.pref_change_primary_color_key), null)
        return ThemeColor.fromString(
            themeColorStr
        )
    }

    fun applyTheme(context: Context): ThemeColor? {
        return getThemeColor(context)?.let {
            context.theme.applyStyle(it.themeResource, true)
            it
        }
    }


    fun applyTheme(fragment: DialogFragment) {
        val context = fragment.context ?: return
        getThemeColor(context)
            ?.let {
                fragment.setStyle(DialogFragment.STYLE_NORMAL, it.themeResource)
            }
    }
}