package net.unsweets.gamma.presentation.util

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

sealed class ColorResource {
    data class Color(@ColorRes val colorRes: Int) : ColorResource() {
        override fun getColor(context: Context): ColorStateList? {
            return ContextCompat.getColorStateList(context, colorRes)
        }
    }

    data class Attr(val attrRes: Int) : ColorResource() {
        override fun getColor(context: Context): ColorStateList? {
//            val typedValue = TypedValue()
//            val theme = context.theme
//            theme.resolveAttribute(attrRes, typedValue, true)
//            return typedValue.data
            TODO()

        }
    }

    abstract fun getColor(context: Context): ColorStateList?
}