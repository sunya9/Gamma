package net.unsweets.gamma.presentation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class DividerIgnoreLastItem(context: Context?, orientation: Int, private val reverse: Boolean = false) :
    DividerItemDecoration(context, orientation) {
    companion object {
        const val HORIZONTAL = LinearLayout.HORIZONTAL
        const val VERTICAL = LinearLayout.VERTICAL
    }

    private var mDivider: Drawable? = null

    private var mOrientation: Int = orientation

    private val mBounds = Rect()

    override fun setDrawable(drawable: Drawable) {
        mDivider = drawable
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || mDivider == null) {
            return
        }
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            // ignore last item
            if (isLastItem(parent, i)) continue
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - mDivider!!.intrinsicHeight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }


    private fun isLastItem(parent: RecyclerView, childItemPos: Int): Boolean {
        val child = parent.getChildAt(childItemPos)
        val pos = parent.getChildAdapterPosition(child)
//        return when (reverse) {
//            true -> pos == 0
//            false -> (parent.adapter?.itemCount ?: 0) - 1 == pos
//        }
        return (parent.adapter?.itemCount ?: 0) - 1 == pos
    }


    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            // ignore last item
            if (isLastItem(parent, i)) continue
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + child.translationX.roundToInt()
            val left = right - mDivider!!.intrinsicWidth
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (mDivider == null) {
            outRect.set(0, 0, 0, 0)
            return
        }
        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
        }
    }
}