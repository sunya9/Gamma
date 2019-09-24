package net.unsweets.gamma.presentation.view

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView


class ClippedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ImageView(context, attrs) {
    init {
        clipToOutline = true
        outlineProvider = OutlineProvider()
//        setBackgroundResource(R.drawable.bg_circle_clipped)
//        outlineProvider = ViewOutlineProvider.BACKGROUND
    }

    private val mBorderRect = RectF()

    private inner class OutlineProvider : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            val bounds = Rect()
            mBorderRect.roundOut(bounds)
            outline.setRoundRect(bounds, bounds.width() / 2.0f)
        }

    }

}