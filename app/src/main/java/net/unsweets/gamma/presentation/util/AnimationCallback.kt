package net.unsweets.gamma.presentation.util

interface AnimationCallback {
    fun onAnimationEnd(open: Boolean)
    fun onAnimationStart(open: Boolean)
}