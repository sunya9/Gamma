package net.unsweets.gamma.util

import android.util.Log
import net.unsweets.gamma.BuildConfig


object LogUtil {
    private val TAG: String? = LogUtil::class.java.canonicalName
    fun e(message: String? = "") {
        if (BuildConfig.DEBUG) Log.e(TAG, message)
    }

    fun d(message: String? = "") {
        if (BuildConfig.DEBUG) Log.d(TAG, message)
    }

    fun i(message: String? = "") {
        if (BuildConfig.DEBUG) Log.i(TAG, message)
    }

    fun w(message: String? = "") {
        if (BuildConfig.DEBUG) Log.w(TAG, message)
    }

    fun v(message: String? = "") {
        if (BuildConfig.DEBUG) Log.v(TAG, message)
    }
}