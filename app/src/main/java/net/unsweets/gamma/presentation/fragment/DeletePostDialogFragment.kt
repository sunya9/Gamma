package net.unsweets.gamma.presentation.fragment


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Post

class DeletePostDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> listener?.ok(position, post)
            DialogInterface.BUTTON_NEGATIVE -> listener?.cancel()
        }
        dismiss()
    }

    private var listener: Callback? = null
    private val position by lazy {
        arguments?.getInt(BundleKey.Position.name) ?: throw NullPointerException("Must set Position")
    }

    private val post by lazy {
        arguments?.getParcelable<Post>(BundleKey.Post.name) ?: throw NullPointerException("Must set Post")
    }

    enum class BundleKey { Position, Post }

    interface Callback {
        fun ok(position: Int, post: Post)
        fun cancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? Callback
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_post)
            .setMessage(R.string.this_operation_cannot_be_undone)
            .setPositiveButton(R.string.ok, this)
            .setNegativeButton(R.string.cancel, this)
            .show()
    }

    companion object {
        fun newInstance(position: Int, post: Post) = DeletePostDialogFragment().apply {
            arguments = Bundle().apply {
                putInt(BundleKey.Position.name, position)
                putParcelable(BundleKey.Post.name, post)
            }
        }
    }
}
