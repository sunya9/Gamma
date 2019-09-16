package net.unsweets.gamma.presentation.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.compose_poll_body_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PollPostBody

class ComposePollViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dragHandleImageButton: AppCompatImageButton =
        itemView.pollBodyItemDragHandleImageButton
    private val textInputEditText: TextInputEditText =
        itemView.pollBodyItemTextInputEditText
    private val pollBodyItemTextInputLayout: TextInputLayout =
        itemView.pollBodyItemTextInputLayout
    private val closeImageButton: AppCompatImageButton =
        itemView.pollBodyItemCloseImageButton

    interface Callback {
        fun removeOption(position: Int)
        fun updateItem(option: PollPostBody.PollOption, position: Int)
    }

    fun bindTo(option: PollPostBody.PollOption, listener: Callback, itemCount: Int) {
        setupEditText(option, listener)
        setupTextInputLayout()
        setupCloseButton(listener, itemCount)
    }

    private fun setupTextInputLayout() {
        pollBodyItemTextInputLayout.hint =
            itemView.context.getString(R.string.choice_template, adapterPosition + 1)
    }

    private fun setupCloseButton(listener: Callback, itemCount: Int) {
        val visibility = when {
            itemCount < 3 -> View.INVISIBLE
            else -> View.VISIBLE
        }
        closeImageButton.visibility = visibility
        closeImageButton.setOnClickListener {
            listener.removeOption(adapterPosition)
        }
    }

    private fun setupEditText(option: PollPostBody.PollOption, listener: Callback) {
        textInputEditText.setText(option.text)
        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener.updateItem(option.copy(text = s?.toString().orEmpty()), position)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}