package net.unsweets.gamma.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class ClippedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ImageView(context, attrs) {
    init {
        clipToOutline = true

    }
}