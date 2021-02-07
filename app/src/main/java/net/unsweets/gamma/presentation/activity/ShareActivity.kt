package net.unsweets.gamma.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import net.unsweets.gamma.R
import net.unsweets.gamma.domain.model.UriInfo
import net.unsweets.gamma.presentation.fragment.ComposePostFragment
import net.unsweets.gamma.presentation.util.ThemeColorUtil

class ShareActivity : DaggerAppCompatActivity() {
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private val text by lazy {
        intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
    }
    private val intentExtraDataList by lazy {
        when (intent.action) {
            Intent.ACTION_SEND -> {
                normalizeMediaFileUriList(arrayListOf(intent.getParcelableExtra(Intent.EXTRA_STREAM)))
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                normalizeMediaFileUriList(intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM))
            }
            else -> null
        }
    }

    private fun normalizeMediaFileUriList(uriList: List<Uri?>?): ArrayList<UriInfo>? {
        if (uriList == null) return null
        return ArrayList(uriList.filterNotNull().map { UriInfo(it) })
    }

    private val composePostFragment by lazy {
        ComposePostFragment.newInstance(
            ComposePostFragment.ComposePostFragmentOption(
            initialText = text,
            intentExtraDataList = intentExtraDataList
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeColorUtil.currentDarkThemeMode(this)
        ThemeColorUtil.applyTheme(this)
        setContentView(R.layout.activity_compose_post)
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.compose_post_placeholder, composePostFragment).commit()
        }
    }
}
