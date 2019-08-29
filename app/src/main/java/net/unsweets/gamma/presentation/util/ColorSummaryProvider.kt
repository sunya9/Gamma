package net.unsweets.gamma.presentation.util

import android.content.Context
import androidx.preference.Preference
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.view.ThemeColorPreference

class ColorSummaryProvider(context: Context) :
    Preference.SummaryProvider<ThemeColorPreference> {
    private val defaultMessage = context.getString(R.string.default_text)
    override fun provideSummary(preference: ThemeColorPreference?): CharSequence {
        val themeColor = preference?.themeColor
        return themeColor?.name ?: defaultMessage
    }

}