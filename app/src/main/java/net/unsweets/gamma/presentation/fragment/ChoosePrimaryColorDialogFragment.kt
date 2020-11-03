package net.unsweets.gamma.presentation.fragment


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_choose_primary_color_dialog.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.presentation.adapter.ColorListAdapter
import net.unsweets.gamma.presentation.util.ThemeColorUtil

class ChoosePrimaryColorDialogFragment : DialogFragment(), DialogInterface.OnClickListener,
    ColorListAdapter.Callback {
    override fun chooseThemeColor(themeColor: ThemeColorUtil.ThemeColor) {
        viewModel.themeColor = themeColor
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> listener?.updateColor(viewModel.themeColor)
            DialogInterface.BUTTON_NEGATIVE -> dismiss()
            DialogInterface.BUTTON_NEUTRAL -> listener?.setAsDefault()
        }
    }

    private var listener: Callback? = null
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ChoosePrimaryColorDialogViewModel.Factory(themeColor)
        )[ChoosePrimaryColorDialogViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface Callback {
        fun updateColor(themeColor: ThemeColorUtil.ThemeColor?)
        fun setAsDefault()
    }

    private val themeColor by lazy {
        arguments?.getSerializable(BundleKey.ThemeColor.name) as? ThemeColorUtil.ThemeColor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.MaterialAlertDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context)
            .inflate(
                R.layout.fragment_choose_primary_color_dialog,
                view?.findViewById(android.R.id.content)
            )
        view.colorList.adapter = ColorListAdapter(this, themeColor)
        view.colorList.setHasFixedSize(true)
        return MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .setPositiveButton(R.string.ok, this)
            .setNeutralButton(R.string.default_text, this)
            .setNegativeButton(R.string.cancel, this)
            .setTitle(R.string.change_theme_color)
            .create()
    }


    class ChoosePrimaryColorDialogViewModel(themeColorArg: ThemeColorUtil.ThemeColor?) :
        ViewModel() {
        var themeColor: ThemeColorUtil.ThemeColor? = themeColorArg

        class Factory(private val themeColor: ThemeColorUtil.ThemeColor?) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ChoosePrimaryColorDialogViewModel(themeColor) as T
            }
        }
    }

    private enum class BundleKey { ThemeColor }

    companion object {
        fun newInstance(themeColor: ThemeColorUtil.ThemeColor?) =
            ChoosePrimaryColorDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(BundleKey.ThemeColor.name, themeColor)
                }
            }
    }

}
