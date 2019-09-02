package net.unsweets.gamma.presentation.util

import android.content.Context
import android.graphics.Path
import android.transition.PathMotion
import android.util.AttributeSet
import net.unsweets.gamma.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class TransitionArcMotion(context: Context, attrs: AttributeSet) : PathMotion(context, attrs) {
    private var curveRadius: Float = 0f

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.TransitionArcMotion)
        curveRadius = a.getInteger(R.styleable.TransitionArcMotion_arcRadius, DEFAULT_RADIUS).toFloat()
        a.recycle()
    }

    override fun getPath(startX: Float, startY: Float, endX: Float, endY: Float): Path {
        val arcPath = Path()

        val midX = startX + (endX - startX) / 2
        val midY = startY + (endY - startY) / 2
        val xDiff = midX - startX
        val yDiff = midY - startY

        val angle = atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI) - 90
        val angleRadians = Math.toRadians(angle)

        val pointX = (midX + curveRadius * cos(angleRadians)).toFloat()
        val pointY = (midY + curveRadius * sin(angleRadians)).toFloat()

        arcPath.moveTo(startX, startY)
        arcPath.cubicTo(startX, startY, pointX, pointY, endX, endY)
        return arcPath
    }

    companion object {
        private const val DEFAULT_RADIUS = 500
    }
}
