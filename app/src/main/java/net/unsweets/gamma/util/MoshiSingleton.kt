package net.unsweets.gamma.util

import com.squareup.moshi.Moshi

object MoshiSingleton {
    val moshi: Moshi = Moshi.Builder().build()
}