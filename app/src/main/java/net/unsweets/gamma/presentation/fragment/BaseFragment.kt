package net.unsweets.gamma.presentation.fragment

import androidx.fragment.app.Fragment
import dagger.android.support.DaggerFragment
import net.unsweets.gamma.domain.repository.IPreferenceRepository
import net.unsweets.gamma.presentation.util.FragmentHelper
import javax.inject.Inject

abstract class BaseFragment : DaggerFragment() {

    @Inject
    lateinit var preferenceRepository: IPreferenceRepository
    protected fun backToPrevFragment() {
        val fm = parentFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        }
    }

    protected fun addFragment(fragment: Fragment, tag: String): Fragment? {
        return FragmentHelper.addFragment(requireContext(), fragment, tag)
    }
}
