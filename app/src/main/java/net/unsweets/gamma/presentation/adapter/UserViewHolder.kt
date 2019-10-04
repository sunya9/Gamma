package net.unsweets.gamma.presentation.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_user_item.view.*
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.Relationship
import net.unsweets.gamma.domain.entity.User
import net.unsweets.gamma.presentation.util.ColorResource
import net.unsweets.gamma.presentation.util.EntityOnTouchListener
import net.unsweets.gamma.presentation.util.GlideApp
import net.unsweets.gamma.presentation.util.Util


class UserViewHolder(mView: View) :
    RecyclerView.ViewHolder(mView) {
    private val avatarView: ImageView = itemView.avatarImageView
    private val screenNameTextView: TextView = itemView.screenNameTextView
    private val handleNameTextView: TextView = itemView.handleNameTextView
    private val bodyTextView: TextView = itemView.bodyTextView
    private val relationshipTextView: TextView = itemView.relationshipTextView
    private val actionButton: MaterialButton = itemView.actionButton
    private val entityListener: View.OnTouchListener = EntityOnTouchListener()
    private val context = itemView.context

    interface Callback {
        fun onActionButtonClick(user: User)
    }

    fun bind(user: User, listener: Callback) {
        GlideApp.with(itemView.context)
            .load(user.content.avatarImage.link)
            .apply(RequestOptions.circleCropTransform())
            .into(avatarView)
        screenNameTextView.text = user.username
        handleNameTextView.text = user.name
        bodyTextView.apply {
            text = user.content.getSpannableStringBuilder(itemView.context)
            setOnTouchListener(entityListener)
        }
        relationshipTextView.visibility = Util.getVisibility(!user.me && user.followsYou)
        val relationshipText = user.relationshipTextRes?.let { context.getString(it) }
        actionButton.text = relationshipText
        actionButton.setOnClickListener {
            listener.onActionButtonClick(user)
        }
        applyStyle(user)
    }

    private fun applyStyle(user: User) {
        val relationship = Relationship.getRelationship(user)
        val style = actionButtonStyleMap[relationship] ?: return
        style.outlineColorRes.getColor(context)?.let {
            actionButton.strokeColor = it

        }
        style.bgColorRes.getColor(context)?.let {
            actionButton.backgroundTintList = it
        }
        style.textColorRes.getColor(context)?.let {
            actionButton.setTextColor(it)
        }
    }

    private val actionButtonStyleMap = mapOf(
        Relationship.Follow to ActionButtonStyle(
            ColorResource.Color(R.color.stroke_button),
            ColorResource.Color(R.color.bg_button_active),
            ColorResource.Color(R.color.text_button_active)
        ),
        Relationship.UnFollow to ActionButtonStyle(
            ColorResource.Color(R.color.stroke_button),
            ColorResource.Color(R.color.bg_button_inactive),
            ColorResource.Color(R.color.text_button_inactive)
        ),
        Relationship.Block to ActionButtonStyle(
            ColorResource.Color(R.color.colorError),
            ColorResource.Color(R.color.bg_button_inactive),
            ColorResource.Color(R.color.colorError)
        )
    )


    private data class ActionButtonStyle(
        val outlineColorRes: ColorResource,
        val bgColorRes: ColorResource,
        val textColorRes: ColorResource
    )

}

