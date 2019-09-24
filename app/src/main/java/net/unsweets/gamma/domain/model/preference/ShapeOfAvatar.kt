package net.unsweets.gamma.domain.model.preference

import androidx.annotation.DrawableRes
import com.makeramen.roundedimageview.RoundedImageView
import net.unsweets.gamma.R
import net.unsweets.gamma.util.oval
import net.unsweets.gamma.util.rounded
import net.unsweets.gamma.util.square


enum class ShapeOfAvatar(@DrawableRes val drawableRes: Int) {
    Circle(R.drawable.bg_avatar_circle),
    Rounded(R.drawable.bg_avatar_rounded),
    Square(R.drawable.bg_avatar_square);

    fun setShape(roundedImageView: RoundedImageView) {
        when (this) {
            Circle -> roundedImageView.oval()
            Rounded -> roundedImageView.rounded()
            Square -> roundedImageView.square()
        }
    }
}