package net.unsweets.gamma.presentation.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.util.ThemeColorUtil


class ThemeColorPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : Preference(context, attrs) {
    var themeColor: ThemeColorUtil.ThemeColor? = null
        get() = ThemeColorUtil.ThemeColor.fromString(getPersistedString(""))
        set(value) {
            persistString(value?.name.orEmpty())
            field = value
            notifyChanged()
        }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        val colorPreviewImageView =
            holder?.findViewById(R.id.colorPreviewImageView) as? ImageView ?: return
        val color = themeColor?.getColor(context) ?: context.getColor(R.color.colorPrimary)
        colorPreviewImageView.setImageDrawable(ColorDrawable(color))
    }
}