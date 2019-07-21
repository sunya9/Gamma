package net.unsweets.gamma.presentation.fragment


import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.interaction_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.entity.Interaction
import net.unsweets.gamma.domain.entity.Interaction.Action
import net.unsweets.gamma.domain.entity.PnutResponse
import net.unsweets.gamma.domain.entity.Post
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.domain.model.io.GetInteractionInputData
import net.unsweets.gamma.domain.model.params.composed.GetInteractionsParam
import net.unsweets.gamma.domain.model.params.single.PaginationParam
import net.unsweets.gamma.domain.usecases.GetInteractionUseCase
import net.unsweets.gamma.presentation.adapter.BaseListRecyclerViewAdapter
import net.unsweets.gamma.presentation.util.DateUtil
import javax.inject.Inject

class InteractionFragment : BaseListFragment<Interaction, InteractionFragment.InteractionViewHolder>(),
    BaseListRecyclerViewAdapter.IBaseList<Interaction, InteractionFragment.InteractionViewHolder> {
    override val itemNameRes: Int = R.string.interactions

    @Inject
    lateinit var getInteractionUseCase: GetInteractionUseCase

    override lateinit var viewModel: InteractionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this, InteractionViewModel.Factory(getInteractionUseCase))
            .get(InteractionViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun createViewHolder(mView: View, viewType: Int): InteractionViewHolder = InteractionViewHolder(mView)

    override fun onClickItemListener(item: Interaction) {
    }

    override fun onBindViewHolder(
        item: Interaction,
        viewHolder: InteractionViewHolder,
        position: Int,
        isMainItem: Boolean
    ) {
        val concreteItem = when (item.action) {
            Action.Bookmark -> item as Interaction.Bookmark
            Action.PollResponse -> item as Interaction.PollResponse
            Action.Follow -> item as Interaction.Follow
            Action.Reply -> item as Interaction.Reply
            Action.Repost -> item as Interaction.Repost
        }
        viewHolder.iconView.setImageResource(item.action.iconRes)
        viewHolder.timeTextView.text = DateUtil.getShortDateStr(viewHolder.itemView.context, item.eventDate)
        viewHolder.messageTextView.text = item.getMessage(viewHolder.itemView.context)
        viewHolder.bodyTextView.text = when (concreteItem) {
            is Interaction.Repost -> handlePost(concreteItem.objects)
            is Interaction.Bookmark -> handlePost(concreteItem.objects)
            is Interaction.Reply -> handlePost(concreteItem.objects)
            is Interaction.Follow -> handleUser(concreteItem.objects)
            is Interaction.PollResponse -> handlePoll(concreteItem.objects)
            is Interaction.HasUsersFieldInteraction -> TODO()
        }
    }

    private fun handlePoll(objects: List<Interaction.PollResponse.InteractionPoll>): CharSequence? {
        return objects[0].prompt
    }

    private fun handleUser(objects: List<User>): CharSequence? {
        return objects[0].username
    }

    private fun handlePost(posts: List<Post>): SpannableStringBuilder? {
        return context?.let { posts[0].content?.getSpannableStringBuilder(it) }
    }

    override fun getItemLayout(): Int = R.layout.interaction_item

    override val baseListListener = this

    class InteractionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.messageTextView
        val iconView: ImageView = view.iconView
        val bodyTextView: TextView = view.bodyTextView
        val timeTextView: TextView = view.timeTextView
    }

    class InteractionViewModel(private val getInteractionUseCase: GetInteractionUseCase) : BaseListViewModel<Interaction>() {
        override suspend fun getItems(pagination: PaginationParam): PnutResponse<List<Interaction>> {
            val params = GetInteractionsParam().apply { add(pagination) }
            Log.e("params", params.toString())
            return getInteractionUseCase.run(GetInteractionInputData(params)).res
        }


        class Factory(private val getInteractionUseCase: GetInteractionUseCase) : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return InteractionViewModel(getInteractionUseCase) as T
            }

        }
    }

    companion object {
        fun newInstance() = InteractionFragment()
    }
}
