package net.unsweets.gamma.domain.model.preference

import androidx.annotation.DrawableRes
import net.unsweets.gamma.R


enum class ShapeOfAvatar(@DrawableRes val drawableRes: Int) {
    Circle(R.drawable.bg_avatar_circle),
    Rounded(R.drawable.bg_avatar_rounded),
    Square(R.drawable.bg_avatar_square)
}