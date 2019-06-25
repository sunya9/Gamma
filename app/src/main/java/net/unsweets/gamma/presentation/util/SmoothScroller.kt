package net.unsweets.gamma.presentation.util

import android.content.Context
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.LinearSmoothScroller

// https://qiita.com/chibatching/items/52d9b73d244eac52d0d4
class SmoothScroller(context: Context) : LinearSmoothScroller(context) {
    init {
        targetPosition = 0
    }

    private var isScrolled: Boolean = false
    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun updateActionForInterimTarget(action: Action) {
        if (isScrolled) {
            action.jumpTo(targetPosition)
        } else {
            super.updateActionForInterimTarget(action)
            action.interpolator = AccelerateInterpolator(1.5F)
            isScrolled = true
        }
    }
}
