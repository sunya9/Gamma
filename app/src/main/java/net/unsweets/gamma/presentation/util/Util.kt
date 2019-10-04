package net.unsweets.gamma.presentation.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.ColorUtils
import androidx.core.view.MenuItemCompat
import net.unsweets.gamma.R

object Util {
    fun showKeyboard(view: View) {
        val imm = getImm(view.context)
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

    }

    fun hideKeyboard(view: View) {
        val imm = getImm(view.context)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun getImm(context: Context) =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

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
        val colorStateList =
            AppCompatResources.getColorStateList(context, R.color.toolbar_icon_tint)
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
                val color = getPrimaryColor(context)
                menuItem.icon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
            false -> {
                menuItem.icon.clearColorFilter()
                val colorStateList =
                    AppCompatResources.getColorStateList(context, R.color.toolbar_icon_tint)
                setTintForToolbarIcon(colorStateList, menuItem)
            }
        }
    }

    fun openCustomTabUrl(context: Context, link: String) {
        try {
            CustomTabsIntent
                .Builder()
                .setShowTitle(true)
//                .setActionButton(icon, menuLabel, pendingIntent, false)
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setToolbarColor(context.getColor(R.color.colorWindowBackground))
                .setStartAnimations(context, R.anim.slide_in_left, R.anim.slide_out_left)
                .setExitAnimations(context, R.anim.slide_in_right, R.anim.slide_out_right)
                .build()
                .launchUrl(context, Uri.parse(link))
        } catch (e: Exception) {
            // TODO: to improve error handling
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun getPrimaryColor(context: Context) = getAttributeValue(context, R.attr.colorPrimary)
    fun getAccentColor(context: Context) = getAttributeValue(context, R.attr.colorAccent)

    private fun getAttributeValue(context: Context, @AttrRes resourceId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(resourceId, typedValue, true)
        return typedValue.data
    }

    fun getPrimaryColorDark(context: Context) =
        ColorUtils.blendARGB(getPrimaryColor(context), Color.BLACK, 0.1f)

    fun getWindowBackgroundColor(context: Context): Int =
        getAttributeValue(context, android.R.attr.windowBackground)

    fun getVisibility(b: Boolean) = if (b) View.VISIBLE else View.GONE
}