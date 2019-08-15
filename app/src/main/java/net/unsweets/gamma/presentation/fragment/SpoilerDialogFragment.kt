package net.unsweets.gamma.presentation.fragment


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.unsweets.gamma.R
import net.unsweets.gamma.databinding.FragmentSpoilerDialogBinding
import net.unsweets.gamma.domain.entity.raw.Spoiler
import net.unsweets.gamma.presentation.util.showKeyboard
import net.unsweets.gamma.util.observeOnce

class SpoilerDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> ok()
            DialogInterface.BUTTON_NEUTRAL -> remove()
            DialogInterface.BUTTON_NEGATIVE -> cancel()
        }
    }

    private fun ok() {
        val topic = viewModel.topic.value.orEmpty()
        val spoiler = when {
            topic.isNotEmpty() -> {
                Spoiler(
                    Spoiler.SpoilerValue(
                        topic, null
                    )
                )
            }
            else -> null
        }
        listener?.onUpdateSpoiler(spoiler)

    }

    private fun remove() {
        listener?.onUpdateSpoiler(null)
    }

    private fun cancel() {
        dismiss()
    }


    private val topicObserver = Observer<String> {
        updateOkButtonEnabled(alertDialog, it.isNotEmpty())
    }

    private fun updateOkButtonEnabled(dialog: AlertDialog?, b: Boolean) {
        dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = b
    }

    private val alertDialog: AlertDialog?
        get() = (dialog as? AlertDialog)

    private fun updateRemoveButtonEnabled(dialog: AlertDialog?, b: Boolean) {
        dialog?.getButton(DialogInterface.BUTTON_NEUTRAL)?.isEnabled = b
    }

    private var listener: Callback? = null
    private val spoiler by lazy {
        arguments?.getParcelable<Spoiler>(BundleKey.Spoiler.name)
    }

    private lateinit var binding: FragmentSpoilerDialogBinding
    private val viewModel by lazy {
        ViewModelProvider(this, SpoilerDialogViewModel.Factory(spoiler))[SpoilerDialogViewModel::class.java]
    }

    interface Callback {
        fun onUpdateSpoiler(spoiler: Spoiler?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.topic.observe(this, topicObserver)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_spoiler_dialog,
            null,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(R.string.spoiler_alert)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel, this)
            .setPositiveButton(R.string.ok, this)

        if (spoiler != null) {
            builder.setNeutralButton(R.string.remove, this)
        }
        val dialog = builder.show()
        updateRemoveButtonEnabled(dialog, spoiler != null)
        updateOkButtonEnabled(dialog, spoiler != null)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        binding.spoilerEditText.requestFocus()
        viewModel.topic.observeOnce(this, Observer {
            binding.spoilerEditText.also { view ->
                view.requestFocus()
                view.setSelection(it.length)
            }
        })
        showKeyboard(binding.spoilerEditText)
        return dialog
    }

    class SpoilerDialogViewModel(private val spoiler: Spoiler?) : ViewModel() {
        val topic = MutableLiveData<String>().also { liveData ->
            spoiler?.let { liveData.value = it.value.topic }
        }

        class Factory(private val spoiler: Spoiler?) :
            ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SpoilerDialogViewModel(spoiler) as T
            }
        }
    }

    private enum class BundleKey { Spoiler }

    companion object {
        fun newInstance(spoiler: Spoiler?) = SpoilerDialogFragment().also {
            it.arguments = Bundle().also { bundle ->
                bundle.putParcelable(BundleKey.Spoiler.name, spoiler)
            }
        }
    }

}
