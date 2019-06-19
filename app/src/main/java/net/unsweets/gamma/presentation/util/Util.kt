package net.unsweets.gamma.presentation.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun showKeyboard(view: View) {
    val imm = getImm(view.context)
    imm.showSoftInput(view, 0)
}

fun showKeyboardForce(context: Context) {
    val imm = getImm(context)
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun hideKeyboard(view: View) {
    val imm = getImm(view.context)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

private fun getImm(context: Context) = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

interface DrawerContentFragment {
    val menuItemId: Int
}

fun getViewPositionOnScreen(view: View): Pair<Int, Int> {
    val pos = IntArray(2)
    view.getLocationOnScreen(pos)
    val cx = pos[0] + view.width / 2
    val cy = pos[1] + view.height / 2
    return Pair(cx, cy)
}