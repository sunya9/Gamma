package net.unsweets.gamma.util

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import net.unsweets.gamma.R

object FragmentHelper {
    fun addFragment(
        context: Context,
        fragment: Fragment,
        tag: String,
        sharedElement: View? = null,
        transitionName: String? = null
    ) {
        val fm = when (context) {
            is Fragment -> context.fragmentManager
            is AppCompatActivity -> context.supportFragmentManager
            else -> null
        } ?: return
        addFragment(fm, fragment, tag, sharedElement, transitionName)
    }

    fun addFragment(
        fm: FragmentManager,
        fragment: Fragment,
        tag: String,
        sharedElement: View? = null,
        transitionName: String? = null
    ) {
        val stackSize = fm.backStackEntryCount
        val last = stackSize - 1
        val uniqueTag = getFragmentTag(tag, stackSize)

        // Prevent to add a fragment that same as last.
        if (last >= 0 && fm.findFragmentByTag(getFragmentTag(tag, last)) != null) {
            return
        }
        val ft = fm
            .beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
        if (sharedElement != null && transitionName != null) {
            ft.setReorderingAllowed(true)
                .addSharedElement(sharedElement, transitionName)
        }
        ft.replace(R.id.fragmentPlaceholder, fragment, uniqueTag)
            .addToBackStack(null)
        ft.commit()
    }

    private fun getFragmentTag(tag: String, index: Int): String = "$tag-$index"

    fun backFragment(fm: FragmentManager?) {
        fm?.let {
            if (it.backStackEntryCount > 0) it.popBackStack()
        }
    }
}