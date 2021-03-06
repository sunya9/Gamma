package net.unsweets.gamma.domain.entity.entities

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import net.unsweets.gamma.presentation.fragment.PostItemFragment
import net.unsweets.gamma.presentation.fragment.ProfileFragment
import net.unsweets.gamma.presentation.util.FragmentHelper
import net.unsweets.gamma.presentation.util.TouchableSpan
import net.unsweets.gamma.presentation.util.Util

interface HaveEntities {
    val entities: Entities?
    val html: String?
    val text: String?
    fun getSpannableStringBuilder(context: Context): SpannableStringBuilder {
        val builder = SpannableStringBuilder(text ?: "")
        val normalColor = Util.getPrimaryColor(context)
        val pressedColor = Util.getPrimaryColorDark(context)
        if (entities == null || text == null) return builder
        entities?.let {
            arrayListOf(it.links, it.mentions, it.tags)
                .flatten()
                .sortedBy { entity -> entity.pos }
                .forEach { entity ->
                    val offset = text!!.offsetByCodePoints(0, entity.pos) - entity.pos
                    val diff = entity.text.codePointCount(0, entity.text.length) - entity.text.length
                    val startPos = entity.pos + offset
                    val endPos = startPos + entity.len + diff
                    builder.setSpan(object : TouchableSpan(normalColor, pressedColor) {
                        override fun onClick(widget: View) {
                            clickedEntity(context, entity)
                        }
                    }, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                }
        }
        return builder
    }

    private fun clickedEntity(context: Context, entity: Entities.SealedEntity) {
        when (entity) {
            is Entities.SealedEntity.LinkEntities -> openLink(context, entity)
            is Entities.SealedEntity.MentionEntities -> openProfile(context, entity.id)
            is Entities.SealedEntity.TagEntities -> showTaggedPosts(context, entity.text)
        }
    }

    companion object {
        private fun showTaggedPosts(context: Context, tag: String) {
            val fragment = PostItemFragment.getTaggedStreamInstance(tag)
            FragmentHelper.addFragment(context, fragment, tag)
        }

        private fun openProfile(context: Context, id: String) {
            val fragment = ProfileFragment.newInstance(id)
            FragmentHelper.addFragment(context, fragment, id)
        }

        private fun openLink(context: Context, entity: Entities.SealedEntity.LinkEntities) {
            val link = entity.link
//            val menuLabel = context.getString(R.string.post)

//            val pendingIntent = ComposePostActivity.shareUrlIntent(context, null, link).run {
//                PendingIntent.getActivity(context, 0, this, 0)
//            }
//            val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_create_black_24dp)
            Util.openCustomTabUrl(context, link)
        }
    }
}