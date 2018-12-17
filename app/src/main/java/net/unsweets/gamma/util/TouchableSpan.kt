package net.unsweets.gamma.util

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan


abstract class TouchableSpan(
    private val mNormalTextColor: Int,
    private val mPressedTextColor: Int,
    private val mPressedBackgroundColor: Int = Color.TRANSPARENT
) : ClickableSpan() {
    private var mIsPressed: Boolean = false

    fun setPressed(isSelected: Boolean) {
        mIsPressed = isSelected
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = if (mIsPressed) mPressedTextColor else mNormalTextColor
        ds.bgColor = if (mIsPressed) mPressedBackgroundColor else Color.TRANSPARENT
        ds.isUnderlineText = false
    }
}
