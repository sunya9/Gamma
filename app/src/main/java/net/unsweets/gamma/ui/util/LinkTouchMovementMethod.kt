package net.unsweets.gamma.ui.util

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.widget.TextView


class LinkTouchMovementMethod : LinkMovementMethod() {
    private var mPressedSpan: TouchableSpan? = null

    override fun onTouchEvent(textView: TextView, spannable: Spannable, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mPressedSpan = getPressedSpan(textView, spannable, event)
            if (mPressedSpan != null) {
                mPressedSpan!!.setPressed(true)
                Selection.setSelection(
                    spannable, spannable.getSpanStart(mPressedSpan),
                    spannable.getSpanEnd(mPressedSpan)
                )
                return true
            }
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            val touchedSpan = getPressedSpan(textView, spannable, event)
            if (mPressedSpan != null && touchedSpan !== mPressedSpan) {
                mPressedSpan!!.setPressed(false)
                mPressedSpan = null
                Selection.removeSelection(spannable)
                return true
            }
        } else {
            if (mPressedSpan != null) {
                mPressedSpan!!.setPressed(false)
                super.onTouchEvent(textView, spannable, event)
            }
            mPressedSpan = null
            Selection.removeSelection(spannable)
        }
        return false
    }

    private fun getPressedSpan(
        textView: TextView,
        spannable: Spannable,
        event: MotionEvent
    ): TouchableSpan? {

        val x = event.x.toInt() - textView.totalPaddingLeft + textView.scrollX
        val y = event.y.toInt() - textView.totalPaddingTop + textView.scrollY

        val layout = textView.layout
        val position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x.toFloat())

        val link = spannable.getSpans<TouchableSpan>(position, position, TouchableSpan::class.java)
        var touchedSpan: TouchableSpan? = null
        if (link.isNotEmpty() && positionWithinTag(position, spannable, link[0])) {
            touchedSpan = link[0]
        }

        return touchedSpan
    }

    private fun positionWithinTag(position: Int, spannable: Spannable, tag: Any): Boolean {
        return position >= spannable.getSpanStart(tag) && position <= spannable.getSpanEnd(tag)
    }
}