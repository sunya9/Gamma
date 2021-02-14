package net.unsweets.gamma.util

import android.util.Log
import net.unsweets.gamma.BuildConfig


object LogUtil {
    private val TAG: String? = LogUtil::class.java.canonicalName
    fun e(message: String? = "") {
        if (!BuildConfig.DEBUG) return
        message?.let { Log.e(TAG, it) }
    }

    fun d(message: String? = "") {
        if (!BuildConfig.DEBUG) return
        message?.let { Log.d(TAG, it) }
    }

    fun i(message: String? = "") {
        if (!BuildConfig.DEBUG) return
        message?.let { Log.i(TAG, it) }
    }

    fun w(message: String? = "") {
        if (!BuildConfig.DEBUG) return
        message?.let { Log.w(TAG, it) }
    }

    fun v(message: String? = "") {
        if (!BuildConfig.DEBUG) return
        message?.let { Log.v(TAG, it) }
    }
}