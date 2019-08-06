package net.unsweets.gamma.presentation.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import net.unsweets.gamma.R

fun showKeyboard(view: View) {
    val imm = getImm(view.context)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)

}

fun hideKeyboard(view: View) {
    val imm = getImm(view.context)
    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
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

fun setTintForToolbarIcons(context: Context, menu: Menu) {
    val colorStateList = AppCompatResources.getColorStateList(context, R.color.toolbar_icon_tint)
    for (i in 0 until menu.size()) {
        setTintForToolbarIcon(colorStateList, menu.getItem(i))
    }
}

fun setTintForToolbarIcon(colorStateList: ColorStateList, menuItem: MenuItem) {
    MenuItemCompat.setIconTintList(menuItem, colorStateList)
}


fun setTintForCheckableMenuItem(context: Context, menuItem: MenuItem) {
    when (menuItem.isChecked) {
        true -> {
            val color = ContextCompat.getColor(context, R.color.colorPrimary)
            menuItem.icon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        false -> {
            menuItem.icon.clearColorFilter()
            val colorStateList = AppCompatResources.getColorStateList(context, R.color.toolbar_icon_tint)
            setTintForToolbarIcon(colorStateList, menuItem)
        }
    }
}

fun openCustomTabUrl(context: Context, link: String) {
    val packageName = CustomTabsClient.getPackageName(context, arrayListOf(context.packageName))
    CustomTabsIntent
        .Builder()
        .setShowTitle(true)
//                .setActionButton(icon, menuLabel, pendingIntent, false)
        .addDefaultShareMenuItem()
        .enableUrlBarHiding()
        .build()
        .also { it.intent.`package` = packageName }
        .launchUrl(context, Uri.parse(link))
}