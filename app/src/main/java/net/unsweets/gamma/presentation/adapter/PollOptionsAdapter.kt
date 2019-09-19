package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.poll_item_view.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.domain.entity.PollLikeValue
import net.unsweets.gamma.util.LogUtil

class PollOptionsAdapter(private val pollLikeValue: PollLikeValue, private var poll: Poll? = null) :
    RecyclerView.Adapter<PollOptionsAdapter.OptionsViewHolder>() {
    val reachedLimit: Boolean
        get() = poll?.maxOptions ?: 1 < choosedPositions.size
    val votable
        get() = !reachedLimit && choosedPositions.size > 0
    private val options
        get() = poll?.options ?: pollLikeValue.options

    enum class ViewType(@LayoutRes val viewRes: Int) {
        Votable(R.layout.poll_item_vote), ViewOnly(R.layout.poll_item_view)
    }

    interface Callback {
        fun onUpdateChoiceState(votable: Boolean)
    }

    var listener: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemViewTypeLocal = itemViewType
        val view = inflater.inflate(R.layout.poll_item_view, parent, false)
//        return when (itemViewTypeLocal) {
//            ViewType.Votable -> VotableViewHolder(view)
//            ViewType.ViewOnly -> OptionsViewHolder(view)
//        }
        return OptionsViewHolder(view)
    }

    fun setPollDetail(poll: Poll) {
        this.poll = poll
        choosedPositions.clear()
        LogUtil.e("poll $poll")
        val positions =
            poll.options.withIndex().filter { it.value.isYourResponse == true }.map { it.index }
        LogUtil.e("positions $positions")
        choosedPositions.addAll(positions)
        notifyDataSetChanged()
    }

    private val itemViewType
        get() = when {
            poll != null && !pollLikeValue.alreadyClosed -> ViewType.Votable
            else -> ViewType.ViewOnly
        }

    private val choosedPositions = mutableSetOf<Int>()
    val getChoosedPositions
        get() = choosedPositions.toSet()

    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        val option = options[position]
        val pollLocal = poll
//        when {
//            holder is VotableViewHolder && pollLocal != null -> holder.bindTo(
//                option,
//                choosedPositions
//            )
//            holder is OptionsViewHolder -> holder.bindTo(option, choosedPositions, pollLocal) { reachedLimit }
//        }
        holder.bindTo(option, choosedPositions, pollLocal) {
            listener?.onUpdateChoiceState(votable)

        }

    }

    class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pollOptionProgressBar: ProgressBar = itemView.pollOptionProgressBar
        private val pollOptionCheckBox: MaterialCheckBox = itemView.pollOptionCheckBox
        private val pollOptionCountTextView: TextView = itemView.pollOptionCountTextView
        private val pollOptionLayout: ViewGroup = itemView.pollOptionLayout
        fun bindTo(
            option: Poll.PollOption,
            choosedPositions: MutableSet<Int>,
            poll: Poll?,
            callback: () -> Unit
        ) {
            val value = option.getPercent(poll?.total)
            LogUtil.e("total ${poll?.total} value $value ${option.respondents}")
            pollOptionProgressBar.progress = value
            pollOptionCheckBox.text = option.text
            pollOptionCountTextView.text =
                itemView.context.getString(R.string.poll_percent_template, value)
            LogUtil.e("alreadyClosed ${poll?.alreadyClosed}")
            val alreadyClosed = poll?.alreadyClosed ?: false
            pollOptionCheckBox.isEnabled = !alreadyClosed
            pollOptionCheckBox.isChecked =
                if (poll?.alreadyClosed == false) choosedPositions.contains(adapterPosition) else option.isYourResponse == true
            pollOptionLayout.setOnClickListener { if (!alreadyClosed) pollOptionCheckBox.performClick() }
            pollOptionCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    choosedPositions.add(adapterPosition)
                } else {
                    choosedPositions.remove(adapterPosition)
                }
                callback()
            }
        }

    }

}
