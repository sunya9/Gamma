package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.PollPostBody

class ComposePollListAdapter(
    private val options: MutableList<PollPostBody.PollOption>,
    private val enableAddOptionButton: MutableLiveData<Boolean>
) :
    RecyclerView.Adapter<ComposePollViewHolder>(), ComposePollViewHolder.Callback {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposePollViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.compose_poll_body_item, parent, false)
        return ComposePollViewHolder(view)
    }

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: ComposePollViewHolder, position: Int) {
        val item = options[position]
        holder.bindTo(item, this, options.size)
    }

    override fun updateItem(option: PollPostBody.PollOption, position: Int) {
        options[position] = option
    }

    override fun removeOption(position: Int) {
        options.removeAt(position)
        notifyItemRemoved(position)
        if (position < options.size)
            notifyItemRangeChanged(position, options.size - 1)
        updateAddButtonState()
    }

    fun addItem() {
        if (options.size >= 10) return
        options.add(PollPostBody.PollOption())
        notifyItemInserted(options.size - 1)
        updateAddButtonState()
    }

    private fun updateAddButtonState() {
        enableAddOptionButton.value = options.size < PollPostBody.MaxOptionSize
    }
}