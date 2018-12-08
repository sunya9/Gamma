package net.unsweets.gamma.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.unsweets.gamma.R

object FragmentHelper {
    fun addFragment(fm: FragmentManager, fragment: Fragment, tag: String?) {
        val index = fm.backStackEntryCount - 1
        if(index >= 0) {
            val backStackEntry = fm.getBackStackEntryAt(index)
            if(backStackEntry.name == tag) return
        }
        fm
            .beginTransaction()
            .replace(R.id.fragmentPlaceholder, fragment)
            .addToBackStack(tag)
            .commit()
    }
}