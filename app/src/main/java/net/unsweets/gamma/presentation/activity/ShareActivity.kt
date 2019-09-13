package net.unsweets.gamma.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import net.unsweets.gamma.domain.model.UriInfo
import net.unsweets.gamma.presentation.fragment.ComposePostDialogFragment
import net.unsweets.gamma.presentation.fragment.ComposePostFragment
import net.unsweets.gamma.presentation.util.ThemeColorUtil

class ShareActivity : DaggerAppCompatActivity(), ComposePostDialogFragment.Callback {
    override fun onDismiss() {
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private val text by lazy {
        intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
    }
    private val intentExtraDataList by lazy {
        when {
            intent.action == Intent.ACTION_SEND -> {
                normalizeMediaFileUriList(arrayListOf(intent.getParcelableExtra(Intent.EXTRA_STREAM)))
            }
            intent.action == Intent.ACTION_SEND_MULTIPLE -> {
                normalizeMediaFileUriList(intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM))
            }
            else -> null
        }
    }

    private fun normalizeMediaFileUriList(uriList: List<Uri?>?): ArrayList<UriInfo>? {
        if (uriList == null) return null
        return ArrayList(uriList.filterNotNull().map { UriInfo(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeColorUtil.currentDarkThemeMode(this)
        ThemeColorUtil.applyTheme(this)
        super.onCreate(savedInstanceState)
        val dm = resources.displayMetrics
        val cx = dm.widthPixels / 2
        val cy = dm.heightPixels / 2
        if (savedInstanceState == null) {
            val option = ComposePostFragment.ComposePostFragmentOption(
                intentExtraDataList = intentExtraDataList,
                initialText = text
            )
            val fragment = ComposePostDialogFragment.newInstance(cx, cy, option)
            fragment.show(supportFragmentManager, ComposePostDialogFragment::class.simpleName)
        }
    }
}
