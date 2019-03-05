package net.unsweets.gamma.ui.util

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
        addFragment(context, fragment, tag, createTransitionMap(sharedElement, transitionName))
    }

    fun addFragment(
        context: Context,
        fragment: Fragment,
        tag: String,
        transitionMap: Map<View, String>?
    ) {
        val fm = getFragmentManagerFromContext(context) ?: return
        addFragment(fm, fragment, tag, transitionMap)
    }

    fun addFragment(
        fm: FragmentManager,
        fragment: Fragment,
        tag: String,
        sharedElement: View? = null,
        transitionName: String? = null
    ) {
        addFragment(fm, fragment, tag, createTransitionMap(sharedElement, transitionName))
    }

    private fun createTransitionMap(sharedElement: View?, transitionName: String?): Map<View, String> {
        val res = HashMap<View, String>()
        if (sharedElement != null && transitionName != null) {
            res[sharedElement] = transitionName
        }
        return res
    }

    private fun getFragmentManagerFromContext(context: Context): FragmentManager? {
        return when (context) {
            is Fragment -> context.fragmentManager
            is AppCompatActivity -> context.supportFragmentManager
            else -> null
        }
    }

    fun addFragment(
        fm: FragmentManager,
        fragment: Fragment,
        tag: String,
        transitionMap: Map<View, String>?
    ) {
        val foundFragment = fm.findFragmentById(R.id.fragmentPlaceholder)
        if (foundFragment == null || foundFragment == fragment || foundFragment.tag == tag) return
        val ft = fm
            .beginTransaction()
//            .setReorderingAllowed(true)
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right
            )
//            .setReorderingAllowed(true)

        transitionMap?.forEach {
            val sharedElement = it.key
            val transitionName = it.value
            ft.addSharedElement(sharedElement, transitionName)
        }
//        ft.hide(foundFragment)
        ft.replace(R.id.fragmentPlaceholder, fragment, tag)
//        if(!fragment.isAdded) {
////                    ft.(foundFragment)
//            ft.add(R.id.fragmentPlaceholder, fragment, tag)
//            ft.addToBackStack(null)
//
//        } else {
//            ft.show(fragment)
//            ft.addToBackStack(null)
//        }
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(tag)
        ft.commit()
    }

    private fun getFragmentTag(tag: String, index: Int): String = "$tag-$index"

    fun backFragment(fm: FragmentManager?) {
        fm?.let {
            if (it.backStackEntryCount > 0) it.popBackStack()
        }
    }
}