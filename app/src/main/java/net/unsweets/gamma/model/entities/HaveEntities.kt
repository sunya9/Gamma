package net.unsweets.gamma.model.entities

import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import net.unsweets.gamma.R
import net.unsweets.gamma.activity.ComposePostActivity
import net.unsweets.gamma.fragment.PostItemFragment
import net.unsweets.gamma.fragment.ProfileFragment
import net.unsweets.gamma.util.FragmentHelper
import net.unsweets.gamma.util.TouchableSpan

interface HaveEntities {
    val entities: Entities?
    val html: String?
    val text: String?
    fun getSpannableStringBuilder(context: Context): SpannableStringBuilder {
        val builder = SpannableStringBuilder(text ?: "")
        val normalColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimary, context.theme)
        val pressedColor = ResourcesCompat.getColor(context.resources, R.color.colorPrimaryDarker, context.theme)
        if (entities == null || text == null) return builder
        var offset = 0
        entities?.let {
            arrayListOf(it.links, it.mentions, it.tags)
                .flatten()
                .sortedBy { entity -> entity.pos }
                .forEach { entity ->
                    val zwj = entity.text.count { char ->  char == '\u200d' }
                    val surrogate = entity.text.count { char -> Character.isSurrogate(char) }
                    val diff = zwj + surrogate / 2
                    val startPos = entity.pos + offset
                    val endPos = startPos + entity.len + diff
                    builder.setSpan(object : TouchableSpan(normalColor, pressedColor) {
                        override fun onClick(widget: View) {
                            clickedEntity(context, entity)
                        }
                    }, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    offset += diff

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
            val text = entity.text
            val menuLabel = context.getString(R.string.post)
            val packageName = CustomTabsClient.getPackageName(context, arrayListOf(context.packageName))

            val pendingIntent = ComposePostActivity.shareUrlIntent(context, null, link).run {
                PendingIntent.getActivity(context, 0, this, 0)
            }
            val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_create_black_24dp)
            CustomTabsIntent
                .Builder()
                .setShowTitle(true)
                .setActionButton(icon, menuLabel, pendingIntent, false)
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .build()
                .also { it.intent.`package` = packageName }
                .launchUrl(context, Uri.parse(link))

        }
    }
}