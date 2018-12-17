package net.unsweets.gamma.fragment


import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.interaction_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.api.PnutResponse
import net.unsweets.gamma.model.Interaction
import net.unsweets.gamma.model.Interaction.Action
import net.unsweets.gamma.model.Post
import net.unsweets.gamma.model.User
import net.unsweets.gamma.model.raw.PollNotice
import net.unsweets.gamma.util.DateUtil
import retrofit2.Call

class InteractionFragment : BaseListFragment<Interaction, InteractionFragment.InteractionViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<Interaction, InteractionFragment.InteractionViewHolder> {
    override fun createViewHolder(mView: View): InteractionViewHolder = InteractionViewHolder(mView)

    override fun onClickItemListener(item: Interaction?) {
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

    override fun getLayout(): Int = R.layout.interaction_item

    override fun getBaseListListener(): BaseListRecyclerViewAdapter.IBaseList<Interaction, InteractionViewHolder> =
        this

    override fun getItems(): Call<PnutResponse<List<Interaction>>> = pnut.getInteractions()

    override fun getPaginateId(item: Interaction): String? = item.paginationId

    class InteractionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.messageTextView
        val iconView: ImageView = view.iconView
        val bodyTextView: TextView = view.bodyTextView
        val timeTextView: TextView = view.timeTextView
    }

    companion object {
        fun newInstance() = InteractionFragment()
    }
}
