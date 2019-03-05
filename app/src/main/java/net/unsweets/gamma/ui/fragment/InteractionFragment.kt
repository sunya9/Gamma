package net.unsweets.gamma.ui.fragment


import android.app.Application
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.interaction_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.model.entity.Interaction
import net.unsweets.gamma.model.entity.Interaction.Action
import net.unsweets.gamma.model.entity.Post
import net.unsweets.gamma.model.entity.User
import net.unsweets.gamma.model.entity.raw.PollNotice
import net.unsweets.gamma.ui.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.ui.base.BaseViewModel
import net.unsweets.gamma.ui.util.DateUtil

class InteractionFragment : BaseListFragment<Interaction, InteractionFragment.InteractionViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<Interaction, InteractionFragment.InteractionViewHolder> {
    override val diffCallback: DiffUtil.ItemCallback<Interaction> = object : DiffUtil.ItemCallback<Interaction>() {
        override fun areItemsTheSame(oldItem: Interaction, newItem: Interaction): Boolean =
            oldItem.paginationId == newItem.paginationId

        override fun areContentsTheSame(oldItem: Interaction, newItem: Interaction): Boolean = oldItem == newItem
    }

    override fun onRefreshed() {
    }

    private val interactionsObserver = Observer<PagedList<Interaction>> { adapter.submitList(it) }

    private lateinit var viewModel: InteractionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(InteractionViewModel::class.java)
        super.onCreate(savedInstanceState)
        if (!isConfigurationChanges) {
            viewModel.init()
            viewModel.intaractions.observe(this, interactionsObserver)
        }
    }

    override fun createViewHolder(mView: View): InteractionViewHolder = InteractionViewHolder(mView)

    override fun onClickItemListener(item: Interaction) {
    }

    override fun onBindViewHolder(item: Interaction, viewHolder: InteractionViewHolder, position: Int) {
        val concreteItem = when (item.action) {
            Action.Bookmark -> item as Interaction.Bookmark
            Action.PollResponse -> item as Interaction.PollResponse
            Action.Follow -> item as Interaction.Follow
            Action.Reply -> item as Interaction.Reply
            Action.Repost -> item as Interaction.Repost
        }
        viewHolder.iconView.setImageResource(item.action.iconRes)
        viewHolder.timeTextView.text = DateUtil.getShortDateStr(item.eventDate)
        viewHolder.messageTextView.text = item.getMessage(context ?: return)
        viewHolder.bodyTextView.text = when (concreteItem) {
            is Interaction.Repost -> handlePost(concreteItem.objects)
            is Interaction.Bookmark -> handlePost(concreteItem.objects)
            is Interaction.Reply -> handlePost(concreteItem.objects)
            is Interaction.Follow -> handleUser(concreteItem.objects)
            is Interaction.PollResponse -> handlePoll(concreteItem.objects)
        }
    }

    private fun handlePoll(objects: List<PollNotice>): CharSequence? {
        return objects[0].value.prompt
    }

    private fun handleUser(objects: List<User>): CharSequence? {
        return objects[0].username
    }

    private fun handlePost(posts: List<Post>): SpannableStringBuilder? {
        return context?.let { posts[0].content?.getSpannableStringBuilder(it) }
    }

    override fun getItemLayout(): Int = R.layout.interaction_item

    override fun getBaseListListener(): BaseListRecyclerViewAdapter.IBaseList<Interaction, InteractionViewHolder> =
        this

    class InteractionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.messageTextView
        val iconView: ImageView = view.iconView
        val bodyTextView: TextView = view.bodyTextView
        val timeTextView: TextView = view.timeTextView
    }

    enum class Event

    class InteractionViewModel(app: Application) : BaseViewModel<Event>(app) {
        lateinit var intaractions: LiveData<PagedList<Interaction>>
        fun init() {
            intaractions = pnutRepository.getInteractions()
        }
    }

    companion object {
        fun newInstance() = InteractionFragment()
    }
}
