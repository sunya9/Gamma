package net.unsweets.gamma.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.poll_item_view.view.*
import kotlinx.android.synthetic.main.poll_item_vote.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Poll
import net.unsweets.gamma.domain.entity.PollLikeValue

class PollOptionsAdapter(private val pollLikeValue: PollLikeValue) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val options = pollLikeValue.options

    enum class ViewType(@LayoutRes val viewRes: Int) {
        Votable(R.layout.poll_item_vote), ViewOnly(R.layout.poll_item_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemViewTypeLocal = itemViewType
        val view = inflater.inflate(itemViewTypeLocal.viewRes, parent, false)
        return when (itemViewTypeLocal) {
            ViewType.Votable -> VotableViewHolder(view)
            ViewType.ViewOnly -> ViewOnlyViewHolder(view)
        }
    }

    var poll: Poll? = null
    fun setPollDetail(poll: Poll) {
        this.poll = poll
    }

    private val itemViewType
        get() = when {
            poll != null && !pollLikeValue.alreadyClosed -> ViewType.Votable
            else -> ViewType.ViewOnly
        }

    val choosedPositions = mutableSetOf<Int>()


    override fun getItemCount(): Int = options.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val option = options[position]
        val pollLocal = poll
        when {
            holder is VotableViewHolder && pollLocal != null -> holder.bindTo(
                option,
                choosedPositions
            )
            holder is ViewOnlyViewHolder -> holder.bindTo(option, choosedPositions, pollLocal)
        }

    }

    class ViewOnlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pollOptionProgressBar: ProgressBar = itemView.pollOptionProgressBar
        private val pollOptionTextTextView: TextView = itemView.pollOptionTextTextView
        private val pollOptionCountTextView: TextView = itemView.pollOptionCountTextView
        private val pollOptionCheckImageView: ImageView = itemView.pollOptionCheckImageView
        fun bindTo(
            option: Poll.PollOption,
            choosedPositions: MutableSet<Int>,
            poll: Poll?
        ) {
            val value = option.getPercent(poll?.total)
            pollOptionProgressBar.progress = value
            pollOptionTextTextView.text = option.text
            pollOptionCountTextView.text =
                itemView.context.getString(R.string.poll_percent_template, value)
            pollOptionCheckImageView.visibility =
                if (option.isYourResponse == true) View.VISIBLE else View.GONE
        }

    }

    class VotableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pollViewChoiceItemCheckBox: MaterialCheckBox =
            itemView.pollViewChoiceItemCheckBox

        fun bindTo(
            option: Poll.PollOption,
            choosedPositions: MutableSet<Int>
        ) {
            pollViewChoiceItemCheckBox.text = option.text
            pollViewChoiceItemCheckBox.isChecked = choosedPositions.contains(adapterPosition)
            pollViewChoiceItemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    choosedPositions.add(adapterPosition)
                } else {
                    choosedPositions.remove(adapterPosition)
                }
            }
        }
    }
}
