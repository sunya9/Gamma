package net.unsweets.gamma.ui.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes

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
    @IdRes
    fun getMenuItemId(): Int
}