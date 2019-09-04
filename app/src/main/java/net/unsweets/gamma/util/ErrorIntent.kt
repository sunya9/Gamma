package net.unsweets.gamma.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import net.unsweets.gamma.BuildConfig

object ErrorIntent {
    const val action = "${BuildConfig.APPLICATION_ID}.Error"
    fun createErrorIntent(t: Throwable?): Intent {
        LogUtil.e(t.toString())
        val message: String = when (t) {
            is ErrorCollections.CommunicationError -> t.errorResponse.meta.errorMessage
            else -> t?.message ?: Constants.unknownError
        }
        return Intent().also {
            it.action = action
            it.putExtra(Intent.EXTRA_TEXT, message)
        }
    }

    fun getIntentFilter() = IntentFilter().also {
        it.addAction(action)
    }

    fun broadcast(context: Context, t: Throwable) {
        val errorIntent = createErrorIntent(t)
        LocalBroadcastManager.getInstance(context).sendBroadcast(errorIntent)
    }

}