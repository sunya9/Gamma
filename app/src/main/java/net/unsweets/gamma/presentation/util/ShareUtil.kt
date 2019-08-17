package net.unsweets.gamma.presentation.util

import android.app.Activity
import androidx.core.app.ShareCompat
import net.unsweets.gamma.R

object ShareUtil {
    enum class ShareType(val titleRes: Int) { Text(R.string.share_text), Url(R.string.share_url) }

    fun launchShareUrlIntent(activity: Activity?, url: String) {
        getShareIntent(activity, url, ShareType.Url)
    }

    fun launchShareTextIntent(activity: Activity?, text: String) {
        getShareIntent(activity, text, ShareType.Text)
    }

    private fun getShareIntent(activity: Activity?, text: String, shareType: ShareType) {
        if (activity == null) return
        return ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setText(text)
            .setChooserTitle(shareType.titleRes)
            .startChooser()
    }
}