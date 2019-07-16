package net.unsweets.gamma.presentation.fragment


import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.unsweets.gamma.R
import java.io.Serializable

class BasicDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> onPositive()
            DialogInterface.BUTTON_NEGATIVE -> onNegative()
            DialogInterface.BUTTON_NEUTRAL -> onNeutral()
        }
        dismiss()
    }

    private fun onPositive() {
        emitResult()
    }

    private fun onNegative() {
        emitResult(resultCode = Activity.RESULT_CANCELED)
    }

    private fun onNeutral() {
        emitResult()
    }

    private enum class IntentKey { Extra }

    private fun emitResult(data: Intent = Intent(), resultCode: Int = Activity.RESULT_OK) {
        data.putExtra(IntentKey.Extra.name, extra)
        parentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    private val title by lazy {
        arguments?.getSerializable(BundleKey.Title.name) as? BundledValue
    }
    private val message by lazy {
        arguments?.getSerializable(BundleKey.Message.name) as? BundledValue
    }
    private val positive by lazy {
        arguments?.getSerializable(BundleKey.Positive.name) as? BundledValue
    }
    private val negative by lazy {
        arguments?.getSerializable(BundleKey.Negative.name) as? BundledValue
    }
    private val neutral by lazy {
        arguments?.getSerializable(BundleKey.Neutral.name) as? BundledValue
    }
    private val requestCode by lazy {
        arguments?.getInt(BundleKey.RequestCode.name) ?: -1
    }
    private val extra by lazy {
        arguments?.getBundle(BundleKey.Extra.name)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(context)
        title?.let {
            when (it) {
                is BundledValue.Literal -> builder.setTitle(it.text)
                is BundledValue.ResourceId -> builder.setTitle(it.resourceId)
            }
        }
        message?.let {
            when (it) {
                is BundledValue.Literal -> builder.setMessage(it.text)
                is BundledValue.ResourceId -> builder.setMessage(it.resourceId)
            }
        }
        positive?.let {
            when (it) {
                is BundledValue.Literal -> builder.setPositiveButton(it.text, this)
                is BundledValue.ResourceId -> builder.setPositiveButton(it.resourceId, this)
            }
        }
        negative?.let {
            when (it) {
                is BundledValue.Literal -> builder.setNegativeButton(it.text, this)
                is BundledValue.ResourceId -> builder.setNegativeButton(it.resourceId, this)
            }
        }
        neutral?.let {
            when (it) {
                is BundledValue.Literal -> builder.setNeutralButton(it.text, this)
                is BundledValue.ResourceId -> builder.setNeutralButton(it.resourceId, this)
            }
        }
        return builder.show()
    }

    private sealed class BundledValue : Serializable {
        data class ResourceId(val resourceId: Int) : BundledValue()
        data class Literal(val text: String) : BundledValue()
    }

    private enum class BundleKey { Title, Message, Positive, Negative, Neutral, RequestCode, Extra }

    class Builder {
        private var title: BundledValue? = null
        private var message: BundledValue? = null
        private var positive: BundledValue? = BundledValue.ResourceId(R.string.ok)
        private var negative: BundledValue? = BundledValue.ResourceId(R.string.cancel)
        private var neutral: BundledValue? = null
        private var extra: Bundle? = null

        fun setTitle(res: Int): Builder {
            title = BundledValue.ResourceId(res)
            return this
        }

        fun setTitle(text: String): Builder {
            title = BundledValue.Literal(text)
            return this
        }

        fun setMessage(res: Int): Builder {
            message = BundledValue.ResourceId(res)
            return this
        }

        fun setMessage(text: String): Builder {
            message = BundledValue.Literal(text)
            return this
        }

        fun setPositive(res: Int): Builder {
            positive = BundledValue.ResourceId(res)
            return this
        }

        fun setPositive(text: String): Builder {
            positive = BundledValue.Literal(text)
            return this
        }

        fun setNegative(res: Int): Builder {
            negative = BundledValue.ResourceId(res)
            return this
        }

        fun setNegative(text: String): Builder {
            negative = BundledValue.Literal(text)
            return this
        }

        fun setNeutral(res: Int): Builder {
            neutral = BundledValue.ResourceId(res)
            return this
        }

        fun setNeutral(text: String): Builder {
            neutral = BundledValue.Literal(text)
            return this
        }

        fun setExtra(serializable: Bundle): Builder {
            extra = serializable
            return this
        }

        fun build(requestCode: Int) = BasicDialogFragment().also {
            it.arguments = Bundle().also { b ->
                b.putSerializable(BundleKey.Title.name, title)
                b.putSerializable(BundleKey.Message.name, message)
                b.putSerializable(BundleKey.Positive.name, positive)
                b.putSerializable(BundleKey.Negative.name, negative)
                b.putSerializable(BundleKey.Neutral.name, neutral)
                b.putInt(BundleKey.RequestCode.name, requestCode)
                b.putBundle(BundleKey.Extra.name, extra)
            }
        }
    }
}
