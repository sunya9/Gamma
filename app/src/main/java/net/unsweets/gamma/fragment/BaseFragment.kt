package net.unsweets.gamma.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import net.unsweets.gamma.R
import net.unsweets.gamma.api.PnutService
import net.unsweets.gamma.util.FragmentHelper
import net.unsweets.gamma.util.PrefManager

abstract class BaseFragment : Fragment(), PnutService.Requestable {
    private lateinit var service: PnutService.IPnutService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        service = pnutService(context ?: return)
    }

    val pnut
        get() = service
    val prefManager
        get() = PrefManager(context!!)

    protected fun backToPrevFragment() {
        val fm = fragmentManager ?: return
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            // if prev fragment not exist, show home fragment
            fm.beginTransaction()
                .replace(R.id.fragmentPlaceholder, HomeFragment.newInstance())
                .commit()
        }
    }

    protected fun addFragment(fragment: Fragment, tag: String?) {
        val fm = fragmentManager ?: return
        FragmentHelper.addFragment(fm, fragment, tag)
    }
}
