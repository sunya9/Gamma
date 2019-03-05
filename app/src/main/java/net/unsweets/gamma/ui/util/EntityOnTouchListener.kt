package net.unsweets.gamma.ui.util

import android.text.Spannable
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

// http://oigami.hatenablog.com/entry/2014/11/08/082615
class EntityOnTouchListener: View.OnTouchListener {
    private val method = LinkTouchMovementMethod()
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(v !is TextView || event == null) return false
        v.apply {
            movementMethod = method
            val res = method.onTouchEvent(v, text as Spannable, event)
            movementMethod = null
            isFocusable = false
            return res
        }
    }
}