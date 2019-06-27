package net.unsweets.gamma.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerFragment
import net.unsweets.gamma.presentation.util.FragmentHelper

abstract class BaseFragment : DaggerFragment() {
    protected var isConfigurationChanges: Boolean = false

    private enum class StateKey { ConfigurationChanges }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isConfigurationChanges = savedInstanceState?.getBoolean(StateKey.ConfigurationChanges.name, false) ?: false

    }

    protected fun backToPrevFragment() {
        val fm = fragmentManager ?: return
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(StateKey.ConfigurationChanges.name, activity?.isFinishing ?: false)
    }

    protected fun addFragment(fragment: Fragment, tag: String) {
        FragmentHelper.addFragment(context!!, fragment, tag)
    }
}
