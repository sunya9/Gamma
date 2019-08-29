package net.unsweets.gamma.presentation.fragment


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import kotlinx.android.synthetic.main.fragment_simple_bottom_sheet_menu.view.*
import net.unsweets.gamma.R

class SimpleBottomSheetMenuFragment : BaseBottomSheetDialogFragment() {


    interface Callback {
        fun onMenuShow(menu: Menu, tag: String?)
        fun onMenuItemSelected(menuItem: MenuItem, tag: String?)
    }

    private var listener: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private val menuRes by lazy {
        arguments?.getInt(BundleKey.Menu.name)
            ?: throw IllegalArgumentException("You must set menu resource")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { onShow() }
        return dialog
    }

    private fun onShow() {
        val menu = view?.navigationView?.menu ?: return
        listener?.onMenuShow(menu, tag)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_simple_bottom_sheet_menu, container, false)
        view.navigationView.inflateMenu(menuRes)
        view.navigationView.setNavigationItemSelectedListener {
            listener?.onMenuItemSelected(it, tag)
            dismiss()
            true
        }
        return view
    }

    private enum class BundleKey { Menu }

    companion object {
        fun newInstance(menuRes: Int) = SimpleBottomSheetMenuFragment().apply {
            arguments = Bundle().apply {
                putInt(BundleKey.Menu.name, menuRes)
            }
        }
    }

}
