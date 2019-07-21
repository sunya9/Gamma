package net.unsweets.gamma.service

import android.app.IntentService
import android.content.Context
import android.content.Intent

class ClearCacheService : IntentService("ClearCacheService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        val cacheDir = baseContext.externalCacheDir ?: return
        cacheDir.listFiles().forEach { it.delete() }
    }

    companion object {
        @JvmStatic
        fun startService(context: Context?) {
            val intent = Intent(context, ClearCacheService::class.java)
            context?.startService(intent)
        }
    }
}
